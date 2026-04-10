import { Component, OnInit } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { NgClass } from '@angular/common';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/costumer.model';

@Component({
  selector: 'app-edit-profile',
  imports: [Menu, ReactiveFormsModule, NgxMaskDirective, NgClass],
  providers: [provideNgxMask()],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {

  perfilForm: FormGroup;
  isEditMode = false;
  customer!: Customer;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private customerService: CustomerService
  ) {
    this.perfilForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefone: [''],
      salario: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      cep: ['', Validators.required],
      rua: [{ value: '', disabled: true }],
      bairro: [{ value: '', disabled: true }],
      numero: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      complemento: [''],
      cidade: [{ value: '', disabled: true }],
      estado: [{ value: '', disabled: true }]
    });
  }

  ngOnInit() {
    const cliente = this.customerService.getClienteLogado();

    if (cliente) {
      this.customer = cliente;

      this.perfilForm.patchValue({
        nome: cliente.name,
        email: cliente.email,
        telefone: '',
        salario: cliente.salary,
        cidade: cliente.city,
        estado: cliente.state
      });
    }
  }

  consultaCEP() {
    const cep = this.perfilForm.get('cep')?.value?.replace(/\D/g, '');
    if (!cep || cep.length < 8) return;

    this.http.get(`https://viacep.com.br/ws/${cep}/json`)
      .subscribe((dados: any) => {
        if (!dados.erro) {
          this.perfilForm.patchValue({
            rua: dados.logradouro,
            bairro: dados.bairro,
            cidade: dados.localidade,
            estado: dados.uf
          });
        } else {
          this.perfilForm.patchValue({ cep: '' });
          alert('CEP não encontrado.');
        }
      });
  }

  toggleEditMode() {
    if (this.isEditMode) {
      this.salvar();
    }
    this.isEditMode = !this.isEditMode;
  }

  salvar() {
    if (this.perfilForm.invalid) return;

    const form = this.perfilForm.getRawValue();

    const clienteAtualizado: Customer = {
      ...this.customer,
      name: form.nome,
      email: form.email,
      salary: Number(form.salario),
      city: form.cidade,
      state: form.estado
    };

    this.customerService.atualizarCliente(clienteAtualizado);

    this.customerService.setClienteLogado(clienteAtualizado.cpf);

    this.customer = clienteAtualizado;

    alert('Dados atualizados!');
  }
}