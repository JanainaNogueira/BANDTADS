import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomersList } from '../../components/customers-list/customers-list';
import { Menu } from '../../components/menu/menu';
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-adm-customers',
  imports: [CommonModule, Menu, CustomersList, FormsModule],
  templateUrl: './adm-customers.html',
  styleUrl: './adm-customers.css', 
})
export class AdmCustomers implements OnInit {

  public customers: Customer[] = [];
  public filteredCustomers: Customer[] = [];
  public searchTerm: string = '';

  ngOnInit(): void {
    this.loadCustomers();
  }

  private loadCustomers(): void {
    // Ordenando por nome crescente
    this.customers = [...MOCK_CUSTOMERS].sort((a, b) =>
      (a.name ?? '').localeCompare(b.name ?? '')
    );

    this.filteredCustomers = [...this.customers];
  }

  public buscarClientes(): void {
    const term = this.searchTerm.toLowerCase().trim();
    const normalizedTerm = term.replace(/\D/g, '');

    if (!term) {
      this.filteredCustomers = [...this.customers];
      return;
    }

    this.filteredCustomers = this.customers.filter(cliente => {
      const customerName = (cliente.name ?? '').toLowerCase();
      const customerCpf = (cliente.cpf ?? '').replace(/\D/g, '');

      return (
        customerName.includes(term) ||
        (normalizedTerm !== '' && customerCpf.includes(normalizedTerm))
      );
    });
  }
}