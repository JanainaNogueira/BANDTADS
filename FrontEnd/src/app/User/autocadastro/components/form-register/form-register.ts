import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { CommonModule } from '@angular/common';
import { FormTitle } from '../../../../components/form-title/form-title';

@Component({
  selector: 'app-form-register',
  imports: [ReactiveFormsModule, ButtonSubmit, CommonModule, FormsModule, FormTitle],
  templateUrl: './form-register.html',
  styleUrl: './form-register.css',
})
export class FormRegister {

  dadosPessoais: FormGroup;
  endereco: FormGroup;

  constructor(private http: HttpClient, private form: FormBuilder) {

    this.dadosPessoais = this.form.group({
      nome: ['', Validators.required],
      cpf: ['', Validators.required],
      telefone: ['', Validators.required],
      email: ['', Validators.required, Validators.email],
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
}
