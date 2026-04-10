import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { CommonModule } from '@angular/common';
import { FormTitle } from '../../../../components/form-title/form-title';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { Router } from '@angular/router';

@Component({
  selector: 'app-form-register',
  imports: [ReactiveFormsModule, ButtonSubmit, CommonModule, FormsModule, FormTitle, NgxMaskDirective],
  providers: [provideNgxMask()],
  templateUrl: './form-register.html',
  styleUrl: './form-register.css',
})
export class FormRegister {

  dadosPessoais: FormGroup;
  endereco: FormGroup;

  constructor(private http: HttpClient, private form: FormBuilder, private router:Router) {

    this.dadosPessoais = this.form.group({
      nome: ['', [Validators.required, Validators.pattern(/^[A-Za-zÀ-ÿ\s]+$/)]],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      telefone: ['', [Validators.required, Validators.minLength(11)]],
      email: ['', [Validators.required, Validators.email]]
    });

    this.endereco = this.form.group({
      cep: ['', Validators.required],
      rua: [{ value: '', disabled: true }],
      cidade: [{ value: '', disabled: true }],
      uf: [{ value: '', disabled: true }],
      numero: ['', Validators.required],
      complemento: ['']
    });

  }

  enviarDados() {
    if (this.endereco.invalid || this.dadosPessoais.invalid) {
      return;
    }

    const dados = {
      ...this.dadosPessoais.value,
      ...this.endereco.getRawValue()
    };

    console.log('Dados enviados:', dados);
    this.router.navigate(['/login']);
  }

  consultaCEP() {
    const cep = this.endereco.get('cep')?.value.replace(/\D/g, '');

    if (!cep || cep.length < 8) {
      return;
    }

    console.log(cep);

    this.http.get(`https://viacep.com.br/ws/${cep}/json`)
      .subscribe((dados: any) => {
        if (!dados.erro) {
          this.endereco.patchValue({
            rua: dados.logradouro,
            cidade: dados.localidade,
            uf: dados.uf
          })
        } 
        else {
          this.endereco.patchValue({
            cep: ''
          });
          alert("CEP não encontrado.");
        }
      });
    }

    paginaAtual = 1;

    irParaPagina2() {
      this.paginaAtual = 2;
    }

    voltarPagina1() {
      this.paginaAtual = 1;
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


}
