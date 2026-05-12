import { Component, OnInit } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ManagerTopPanel } from '../componente/manager-top-panel/manager-top-panel';
import { CustomersHome } from './components/customers-home/customers-home';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';
import { Status } from '../../models/status-enum.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-home-gerente',
  imports: [Menu, MatIconModule, CommonModule, ManagerTopPanel, CustomersHome],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente implements OnInit {
  customers: Customer[] = [];

  constructor(private customerService: CustomerService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.carregarPendentes();
  }

  carregarPendentes() {
    this.customerService.obterClientesPendentes().subscribe({
      next: (resp) => {
        this.customers = resp;
      },
      error: (err) => {
        console.error('Erro ao carregar clientes pendentes', err);
        this.showMessage('Erro ao carregar clientes pendentes');
      }
    });
  }

  private showMessage(message: string) {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['text-white', 'rounded-3xl']
    });
  }
}
