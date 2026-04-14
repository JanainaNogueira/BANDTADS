import { Component, ViewChild } from '@angular/core';
import { FormLogin } from './components/form-login/form-login';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AuthSidePanel } from '../../components/auth-side-panel/auth-side-panel';
import { FormTitle } from '../../components/form-title/form-title';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login',
  imports: [FormLogin, CommonModule, MatIconModule, AuthSidePanel, FormTitle],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  @ViewChild(FormLogin) formLogin!: FormLogin;

  constructor(
    private router: Router,
    private loginService: LoginService
  ) {}

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

  fazerLogin(dados: { email: string; senha: string; lembrar: boolean }): void {
    this.loginService.login(dados.email, dados.senha).subscribe({
      next: (usuario) => {
        const tipoResposta = this.normalizarTipo(usuario.tipo || this.tipo);

        if (tipoResposta !== this.tipo) {
          this.erroLogin = true;
          return;
        }

        this.erroLogin = false;
        localStorage.setItem('tipoUsuario', tipoResposta);
        localStorage.setItem('email', usuario.login || dados.email);
        localStorage.setItem('nome', usuario.login || dados.email);

        if (tipoResposta === 'cliente') {
          this.router.navigate(['/home']);
        } else if (tipoResposta === 'gerente') {
          this.router.navigate(['/home-gerente']);
        } else {
          this.router.navigate(['/home-admin']);
        }
      },
      error: () => {
        this.erroLogin = true;
      },
    });
  }

  private normalizarTipo(tipo: string): string {
    const tipoNormalizado = tipo.toLowerCase().trim();

    if (tipoNormalizado === 'admin' || tipoNormalizado === 'administrador') {
      return 'admin';
    }

    if (tipoNormalizado === 'gerente' || tipoNormalizado === 'manager') {
      return 'gerente';
    }

    return 'cliente';
  }
}
