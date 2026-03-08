import { Component } from '@angular/core';
import { FormTitle } from '../../components/form-title/form-title';
import { FormRegister } from './components/form-register/form-register';
import { AuthSidePanel } from '../../components/auth-side-panel/auth-side-panel';
import { MatIconModule } from '@angular/material/icon';
import { FormLogin } from '../login/components/form-login/form-login';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-autocadastro',
  imports: [FormTitle, FormRegister, AuthSidePanel, MatIconModule, FormLogin, CommonModule, ],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro {

}
