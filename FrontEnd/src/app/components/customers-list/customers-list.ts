import { CurrencyPipe} from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CpfPipe } from '../../pipes/cpf.pipe';
import { MatIconModule } from '@angular/material/icon';
import { Customer } from '../../models/customer.model';


@Component({
  selector: 'app-customers-list',
  imports: [CurrencyPipe, MatIconModule, CpfPipe],
  templateUrl: './customers-list.html',
  styleUrl: './customers-list.css',
})

export class CustomersList implements OnChanges {
  @Input() customers: Customer[] = [];
  @Input() view: 'admin' | 'manager' = 'manager';
  @Output() customerSelected = new EventEmitter<Customer>();

  sortDirection: 'asc' | 'desc' = 'asc';

  currentPage = 1;
  itemsPerPage = 10; 

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['customers']) {
      this.currentPage = 1;
    }
  }

  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.currentPage = 1;
  }

  selectCustomer(customer: Customer): void {
    this.customerSelected.emit(customer);
  }

  get sortedCustomers() {
    return [...this.customers].sort((a, b) => {
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
}

