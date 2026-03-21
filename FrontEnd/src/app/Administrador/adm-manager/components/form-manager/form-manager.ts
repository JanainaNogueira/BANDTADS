import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { CommonModule } from '@angular/common';
import { FormTitle } from '../../../../components/form-title/form-title';
import { NgxMaskDirective } from 'ngx-mask';
import { MatIcon } from '@angular/material/icon';

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

  constructor(private http: HttpClient, public form: FormBuilder) {
    this.dadosPessoais = this.form.group({
      nome: ['', [Validators.required, Validators.pattern(/^[A-Za-zÀ-ÿ\s]+$/), Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      telefone: ['', [Validators.required, Validators.minLength(11)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', [Validators.required]]
    },  { validators: this.senhasIguais });
  }

  enviarDados() {
    if (this.dadosPessoais.invalid) return;

    const dados = this.dadosPessoais.value;
  
    delete dados.confirmarSenha;

    console.log(dados);
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
