import { HttpClient } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { CommonModule } from '@angular/common';
import { FormTitle } from '../../../../components/form-title/form-title';
import { NgxMaskDirective } from 'ngx-mask';
import { MatIcon } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-form-manager',
  imports: [ReactiveFormsModule, ButtonSubmit, CommonModule, FormsModule, NgxMaskDirective, MatIcon],
  templateUrl: './form-manager.html',
  styleUrl: './form-manager.css',
})
export class FormManager {
  dadosPessoais: FormGroup;
  mostrarSenha = false;
  mostrarConfirmarSenha  = false;

  constructor(
    public form: FormBuilder,
    private dialogRef: MatDialogRef<FormManager>,
    @Inject(MAT_DIALOG_DATA) public data: {
        modo: 'criar' | 'editar';
        gerente?: any;
      }
  ) {
    this.dadosPessoais = this.form.group({
      nome: ['', [Validators.required, Validators.pattern(/^[A-Za-zÀ-ÿ\s]+$/), Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      telefone: ['', [Validators.required, Validators.minLength(11)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', [Validators.required]]
    },  { validators: this.senhasIguais });

    if (this.data?.modo === 'editar' && this.data.gerente) {
      this.dadosPessoais.patchValue({
        nome: this.data.gerente.nome,
        cpf: this.data.gerente.cpf,
        telefone: this.data.gerente.telefone,
        email: this.data.gerente.email,
        senha: this.data.gerente.senha
      });
    }
  }

  enviarDados() {
    if (this.dadosPessoais.invalid) return;

    const dados = this.dadosPessoais.getRawValue();
    delete dados.confirmarSenha;

    if (this.data.modo === 'editar') {
      console.log('Editando gerente', dados);
    } else {
      console.log('Criando gerente', dados);
    }

    this.dialogRef.close(dados);
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
