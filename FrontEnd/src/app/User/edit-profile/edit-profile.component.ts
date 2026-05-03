import { Component, OnInit } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { NgClass, CommonModule } from '@angular/common';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';
import { CepService } from '../../services/cep.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-profile',
  imports: [Menu, ReactiveFormsModule, NgxMaskDirective, CommonModule, NgClass],
  providers: [provideNgxMask()],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {

  perfilForm: FormGroup;
  isEditMode = false;
  customer?: Customer;

  constructor(
    private fb: FormBuilder,
    private cepService: CepService,
    private customerService: CustomerService,
    private snackBar: MatSnackBar
  ) {
    this.perfilForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', [Validators.required, Validators.minLength(11)]],
      salario: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      cep: ['', Validators.required],
      rua: [{ value: '', disabled: true }],
      numero: ['', Validators.required],
      complemento: [''],
      cidade: [{ value: '', disabled: true }],
      estado: [{ value: '', disabled: true }]
    });
  }


  ngOnInit() {
    this.customerService.getClienteLogado().subscribe({
      next: (cliente) => {
        if (cliente) {
          this.customer = cliente;
          this.perfilForm.patchValue({
            nome: cliente.name,
            email: cliente.email,
            telefone: cliente.telephone,
            salario: cliente.salary,
            cidade: cliente.city,
            estado: cliente.state
          });
        }
      },
      error: () => {
        this.showMessage('Erro ao carregar dados do cliente');
      }
    });
  }

  consultaCEP() {
    const cep = this.perfilForm.get('cep')?.value?.replace(/\D/g, '');
    if (!cep || cep.length < 8) return;

    this.cepService.buscarCep(cep).subscribe({
      next: (dados) => {
        if (!dados.erro) {
          this.perfilForm.patchValue({
            rua: dados.logradouro,
            cidade: dados.localidade,
            estado: dados.uf
          });
        } else {
          this.perfilForm.patchValue({ cep: '' });
          this.showMessage('CEP não encontrado.');
        }
      },
      error: () => {
        this.showMessage('Erro ao buscar CEP.');
      }
    });
  }

  toggleEditMode() {
    if (this.isEditMode) {
      this.salvar();
    } else {
      this.isEditMode = true;
    }
  }

  salvarEFechar() {
    this.salvar();
  }

  salvar() {
    if (this.perfilForm.invalid || !this.customer) return;

    const form = this.perfilForm.getRawValue();

    const clienteAtualizado: Customer = {
      ...this.customer,
      name: form.nome,
      email: form.email,
      telephone: form.telefone,
      salary: Number(form.salario),
      city: form.cidade,
      state: form.estado
    };

    this.customerService.atualizarCliente(clienteAtualizado).subscribe({
      next: () => {
        this.customerService.setClienteLogado(clienteAtualizado.cpf);
        this.customer = clienteAtualizado;
        this.isEditMode = false;
        this.showMessage('Dados atualizados!');
      },
      error: () => {
        this.showMessage('Erro ao atualizar dados');
      }
    });
  }

  private showMessage(message: string) {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['text-white', 'rounded-3xl']
    });
  }

  formatCpf(cpf: string): string {
    if (!cpf || cpf.length !== 11) return '';
    return cpf
      .replace(/\D/g, '')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  }
}
