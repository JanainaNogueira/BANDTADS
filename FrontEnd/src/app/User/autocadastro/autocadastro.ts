import { Component } from '@angular/core';
import { FormRegister } from './components/form-register/form-register';
import { AuthSidePanel } from '../../components/auth-side-panel/auth-side-panel';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-autocadastro',
  imports: [FormRegister, AuthSidePanel, MatIconModule, CommonModule,],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro {

}
