import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Operacoes } from '../../components/operacoes/operacoes';
import { Router } from '@angular/router';
import { MOCK_LOGIN_USER, MOCK_TRANSACTION_USER } from '../../../assets/mock/customers.mock';

@Component({
  selector: 'app-home',
  imports: [Menu, CommonModule, MatIconModule, MatDialogModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(private dialog: MatDialog, private router: Router) {}

  login = MOCK_LOGIN_USER;

  transactions = MOCK_TRANSACTION_USER;

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
