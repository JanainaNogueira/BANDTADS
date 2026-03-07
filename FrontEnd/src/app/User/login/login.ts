import { Component, ViewChild } from '@angular/core';
import { FormLogin } from './components/form-login/form-login';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-login',
  imports: [FormLogin, CommonModule, MatIconModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  @ViewChild(FormLogin) formLogin!: FormLogin;

  tipo = 'cliente';

  tipos = [
    { label: 'Cliente', valor: 'cliente' },
    { label: 'Gerente', valor: 'gerente' },
    { label: 'Administrador', valor: 'admin' }
  ];

  selecionarTipo(tipo: string){
    this.tipo = tipo;

    if(this.formLogin){
      this.formLogin.resetForm(); 
    }
  }

  fazerLogin(dados:any){
    console.log('tipo:', this.tipo);
    console.log(dados);
  }

}
