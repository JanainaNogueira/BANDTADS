import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Operacoes } from '../../components/operacoes/operacoes';
import { Router } from '@angular/router';
import { MOCK_CUSTOMERS, MOCK_LOGIN_USER, MOCK_TRANSACTION_USER } from '../../../assets/mock/customers.mock';
import { MOCK_MANAGERS_LIST } from '../../../assets/mock/managers.mock';
import { MOCK_ADMINS } from '../../../assets/mock/admin.mock';
import { Customer } from '../../models/costumer.model';

@Component({
  selector: 'app-home',
  imports: [Menu, CommonModule, MatIconModule, MatDialogModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(private dialog: MatDialog, private router: Router) {}

  cliente = MOCK_CUSTOMERS;
  gestores = MOCK_MANAGERS_LIST;
  admin = MOCK_ADMINS;

  transactions = MOCK_TRANSACTION_USER;

  email: string = '';
  tipo: string = "";
  login:Customer | null = null;

  ngOnInit() {
    this.email = localStorage.getItem('email') || '';
    this.tipo = localStorage.getItem('tipoUsuario') || "";
    if(this.tipo == "cliente"){
      this.login = this.cliente.find(l =>l.email == this.email) || null;
    }

  }

  abrirOperacoes(tabInicial: number): void {
    this.dialog.open(Operacoes, {
      data: { tabInicial },
      width: '760px',
      maxWidth: '96vw'
    });
  };

  irParaExtrato(): void {
    this.router.navigate(['/bank-statement']);
  }
}
