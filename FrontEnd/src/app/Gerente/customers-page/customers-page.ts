import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Menu } from '../../components/menu/menu';
import { CustomersList } from '../../components/customers-list/customers-list';
import { ManagerTopPanel } from '../componente/manager-top-panel/manager-top-panel';
import { TopClientes } from './top-clientes/top-clientes';
import { ManagerConsultarCliente } from '../manager-consultar-cliente/manager-consultar-cliente';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-customers-page',
  imports: [Menu, ManagerTopPanel, CustomersList, FormsModule, CommonModule, TopClientes, ManagerConsultarCliente],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit {
  customers: Customer[] = [];
  searchTerm: string = '';
  page:number=1;
  selectedCustomer: Customer | null = null;
  private readonly brlFormatter = new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });

  constructor(
    private readonly customerService: CustomerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.customerService.obterTodosClientes().subscribe({
      next: (clientes) => {
        this.customers = clientes.sort((a, b) => a.name.localeCompare(b.name));
      },
      error: (err) => console.error('Erro ao carregar clientes', err)
    });

    this.route.url.subscribe((segments) => {
      const path = segments[0]?.path;
      this.page = path === 'gerente-consultar-cliente' ? 2 : 1;
    });
  }

  get filteredCustomers(): Customer[] {
    if (!this.searchTerm.trim()) return this.customers;

    const searchLower = this.normalizeText(this.searchTerm);
    const searchNumbers = this.searchTerm.replace(/\D/g, '');

    return this.customers.filter(customer => {
      const nameMatch = this.normalizeText(customer.name || '').includes(searchLower);
      const cpfNumbers = (customer.cpf || '').replace(/\D/g, '');
      const cpfMatch = searchNumbers ? cpfNumbers.includes(searchNumbers) : false;
      
      return nameMatch || cpfMatch;
    });
  }

  private normalizeText(value: string): string {
    return value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
  }

  switchPage(page: number): void {
    this.page = page;
    this.selectedCustomer = null;
    const routePath = page === 1 ? 'gerente-clientes' : 'gerente-consultar-cliente';
    this.router.navigate([routePath]);
  }

  openCustomerDetails(customer: Customer): void {
    this.selectedCustomer = customer;
  }

  closeCustomerDetails(): void {
    this.selectedCustomer = null;
  }

  formatCpf(cpf: string): string {
    const digits = (cpf || '').replace(/\D/g, '').slice(0, 11);
    if (digits.length !== 11) return cpf || '-';
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9, 11)}`;
  }

  formatCurrency(value: number): string {
    return this.brlFormatter.format(value || 0);
  }
}