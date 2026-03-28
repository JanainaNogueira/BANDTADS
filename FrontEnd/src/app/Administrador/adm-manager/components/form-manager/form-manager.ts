import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective } from 'ngx-mask';
import { MatIcon } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ManagerCreateEditList } from '../../../../models/manager.model';

@Component({
  selector: 'app-form-manager',
  imports: [ReactiveFormsModule, ButtonSubmit, CommonModule, FormsModule, NgxMaskDirective, MatIcon],
  templateUrl: './form-manager.html',
  styleUrl: './form-manager.css',
})
export class FormManager implements OnInit {
  dadosPessoais: FormGroup;
  mostrarSenha = false;
  mostrarConfirmarSenha  = false;

  constructor(
    public form: FormBuilder,
    private dialogRef: MatDialogRef<FormManager>,
    @Inject(MAT_DIALOG_DATA) public data: {
      modo: 'criar' | 'editar';
      gerente?: ManagerCreateEditList;
    }
  ) {
    this.dadosPessoais = this.form.group({
      nome: ['', [Validators.required, Validators.pattern(/^[A-Za-zÀ-ÿ\s]+$/), Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      telefone: ['', [Validators.required, Validators.minLength(11)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', [Validators.required]]
    }, { validators: this.senhasIguais });
  }

  ngOnInit(): void {
    if (this.data?.modo === 'editar' && this.data.gerente) {

      this.dadosPessoais.patchValue({
      nome: this.data.gerente.nome,
      email: this.data.gerente.email,
      telefone: this.data.gerente.telefone,
      cpf: this.data.gerente.cpf,
      senha: this.data.gerente.senha || '',
      confirmarSenha: this.data.gerente.senha || '' 
    });

      this.dadosPessoais.markAllAsTouched();
    }
  }


  enviarDados() {
    if (this.dadosPessoais.invalid) {
      this.dadosPessoais.markAllAsTouched();
      return;
    }
    const { confirmarSenha, ...dadosLimpos } = this.dadosPessoais.getRawValue();

    this.dialogRef.close({
      modo: this.data.modo,
      gerente: dadosLimpos,
      id: this.data.gerente?.id 
    });
  }

  fechar() {
    this.dialogRef.close();
  }

  bloquearNumeros(event: KeyboardEvent) {
    const char = event.key;
    if (!/[a-zA-ZÀ-ÿ\s]/.test(char)) {
      event.preventDefault();
    }
  }

  campoInvalido(campo: string) {
    const control = this.dadosPessoais.get(campo);
    return control?.invalid && control?.touched;
  }

  campoTemErro(campo: string, erro: string) {
    const control = this.dadosPessoais.get(campo);
    return control?.touched && control?.hasError(erro);
  }

  senhasIguais(form: FormGroup) {
    const senha = form.get('senha')?.value;
    const confirmar = form.get('confirmarSenha')?.value;

    if (senha !== confirmar) {
      return { senhasDiferentes: true };
    }

    return null;
  }

  toggleSenha(tipo:string) {
    if(tipo == 'senha'){
      this.mostrarSenha = !this.mostrarSenha;
    }
    if(tipo == 'confirmarSenha'){
      this.mostrarConfirmarSenha  = !this.mostrarConfirmarSenha ;
    }
  }

}
