import { Component, OnInit } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { MOCK_CUSTOMERS } from '../../../../assets/mock/customers.mock';
import { CommonModule } from '@angular/common';
import { Customer } from '../../../models/customer.model';

@Component({
  selector: 'app-top-clientes',
  standalone: true,
  imports: [MatIcon, CommonModule],
  templateUrl: './top-clientes.html',
  styleUrl: './top-clientes.css',
})
export class TopClientes implements OnInit {

  top3: Customer[] = [];

  ngOnInit(): void {
    this.top3 = this.getTop3Clientes();
  }

  private getTop3Clientes(): Customer[] {
    return [...MOCK_CUSTOMERS]
      .sort((a, b) => b.balance - a.balance)
      .slice(0, 3);
  }

  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}