import { CurrencyPipe} from '@angular/common';
import { Component, Input } from '@angular/core';
import { CpfPipe } from '../../pipes/cpf.pipe';
import { MatIconModule } from '@angular/material/icon';

export interface Manager {
  name: string;
  cpf: string;
}

export interface Customer {
  cpf: string;
  name: string;
  email: string;
  salary: number;
  numberAccount: number;
  balance: number;
  limit: number;
  city?: string;
  state?: string;
  manager: Manager;
}

@Component({
  selector: 'app-customers-list',
  imports: [CurrencyPipe, MatIconModule, CpfPipe],
  templateUrl: './customers-list.html',
  styleUrl: './customers-list.css',
})

export class CustomersList {
  @Input() customers: Customer[] = [];
  @Input() view: 'admin' | 'manager' = 'manager';

  sortDirection: 'asc' | 'desc' = 'asc';

  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
  }

  get sortedCustomers() {
  return [...this.customers].sort((a, b) => {
    const result = a.name.localeCompare(b.name);

    return this.sortDirection === 'asc' ? result : -result;
  });
}
}
