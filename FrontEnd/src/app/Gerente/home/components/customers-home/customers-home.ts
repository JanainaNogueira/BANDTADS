import { CurrencyPipe} from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CpfPipe } from '../../../../pipes/cpf.pipe';
import { ModalRecusar } from '../modal-recusar/modal-recusar';
import { Status } from '../../../../models/status-enum.model';

export interface Manager {
  name: string;
  cpf: string;
}

export interface Customer {
  cpf: string;
  name: string;
  email: string;
  salary: number;
  status: Status;
}

@Component({
  selector: 'app-customers-home',
  imports: [CurrencyPipe, MatIconModule, CpfPipe, ModalRecusar],
  templateUrl: './customers-home.html',
  styleUrl: './customers-home.css',
})

export class CustomersHome {
  @Input() customers: Customer[] = [];

  sortDirection: 'asc' | 'desc' = 'asc';

  currentPage = 1;
  itemsPerPage = 5; 

  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.currentPage = 1;
  }

  get filteredCustomers() {
    return this.customers.filter(c => c.status === Status.PENDENTE);
  }

  get sortedCustomers() {
    return [...this.filteredCustomers].sort((a, b) => {
      const result = a.name.localeCompare(b.name);

      return this.sortDirection === 'asc' ? result : -result;
    });
  }
  
  get totalPages() {
    return Math.ceil(this.sortedCustomers.length / this.itemsPerPage);
  }
  
  get paginatedCustomers() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;

    return this.sortedCustomers.slice(start, end);
  }

  modalAberto = false;

  abrirModal() {
    this.modalAberto = true;
  }

  fecharModal() {
    this.modalAberto = false;
  }

  aprovarCadastro() {

  }
}
