import { Component, ViewChild } from '@angular/core';
import { FormLogin } from './components/form-login/form-login';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AuthSidePanel } from '../../components/auth-side-panel/auth-side-panel';
import { FormTitle } from '../../components/form-title/form-title';
import { Router } from '@angular/router';
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';
import { MOCK_MANAGERS } from '../../../assets/mock/managers.mock';
import { MOCK_ADMINS } from '../../../assets/mock/admin.mock';

@Component({
  selector: 'app-login',
  imports: [FormLogin, CommonModule, MatIconModule, AuthSidePanel, FormTitle],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  @ViewChild(FormLogin) formLogin!: FormLogin;

  constructor(private router: Router) { }

  tipo = 'cliente';
  erroLogin = false;

  tipos = [
    { label: 'Cliente', valor: 'cliente' },
    { label: 'Gerente', valor: 'gerente' },
    { label: 'Administrador', valor: 'admin' }
  ];

  selecionarTipo(tipo: string) {
    this.tipo = tipo;

    if (this.formLogin) {
      this.formLogin.resetForm();
    }

    this.erroLogin = false;
  }

  fazerLogin(dados: any) {
    let lista: any[] = [];

    if (this.tipo === 'cliente') {
      lista = MOCK_CUSTOMERS;
    } else if (this.tipo === 'gerente') {
      lista = MOCK_MANAGERS;
    } else if (this.tipo === 'admin') {
      lista = MOCK_ADMINS;
    }

    const usuario = lista.find(
      u => u.email === dados.email && u.senha === dados.senha
    );

    if (usuario) {
      this.erroLogin = false;

      localStorage.setItem('tipoUsuario', this.tipo);
      localStorage.setItem('email', usuario.email);
      localStorage.setItem('nome', usuario.nome || usuario.name);

      if (this.tipo === 'cliente') {
        this.router.navigate(['/home']);
      } else if (this.tipo === 'gerente') {
        this.router.navigate(['/home-gerente']);
      } else {
        this.router.navigate(['/home-admin']);
      }
    } else {
      this.erroLogin = true;
    }
  }
}