import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';

@Component({
  selector: 'app-form-login',
  imports: [CommonModule, FormsModule, MatIconModule, ButtonSubmit],
  templateUrl: './form-login.html',
  styleUrl: './form-login.css',
})
export class FormLogin {
  @Output() loginSubmit = new EventEmitter<any>();
  @Input() erroLogin = false;


  mostrarSenha = false;
  lembrarLogin = false;
  email = '';
  senha = '';

  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }
  
  resetForm(){
    this.email = '';
    this.senha = '';
    this.lembrarLogin = false;
  }

  get formValido(): boolean {
    return this.email.includes('@') && this.senha.trim() !== '';
  }

  enviarLogin(){
    this.loginSubmit.emit({
      email: this.email,
      senha: this.senha,
      lembrar: this.lembrarLogin
    });
  }
}