import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Menu } from '../../components/menu/menu';
import { Customer } from '../../models/customer.model';
import { CustomerService } from '../../services/customer.service';

interface ClienteRelatorio extends Customer {
  managerName?: string;
  managerCpf?: string;
}

@Component({
  selector: 'app-adm-customers',
  imports: [CommonModule, Menu, FormsModule, MatIconModule],
  templateUrl: './adm-customers.html',
  styleUrls: ['./adm-customers.css'],
})
export class AdmCustomers implements OnInit {

  public customers: ClienteRelatorio[] = [];
  public filteredCustomers: ClienteRelatorio[] = [];
  public searchTerm: string = '';
  public carregando = true;
  public page = 1;
  public pageSize = 10;
  public Math = Math;

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  private loadCustomers(): void {
    this.carregando = true;
    this.customerService.obterTodosClientes().subscribe({
      next: (clientes) => {
        this.customers = clientes
          .sort((a, b) => (a.name ?? '').localeCompare(b.name ?? ''))
          .map(c => ({
            ...c,
            managerName: c.manager?.name || '-',
            managerCpf: c.manager?.cpf || '-'
          }));
        
        this.filteredCustomers = [...this.customers];
        this.carregando = false;
      },
      error: (err) => {
        console.error('Erro ao carregar clientes', err);
        this.carregando = false;
      }
    });
  }

  public buscarClientes(): void {
    const term = this.searchTerm.toLowerCase().trim();
    const normalizedTerm = term.replace(/\D/g, '');

    if (!term) {
      this.filteredCustomers = [...this.customers];
      this.page = 1;
      return;
    }

    this.filteredCustomers = this.customers.filter(cliente => {
      const customerName = (cliente.name ?? '').toLowerCase();
      const customerCpf = (cliente.cpf ?? '').replace(/\D/g, '');
      const managerName = (cliente.managerName ?? '').toLowerCase();

      return (
        customerName.includes(term) ||
        (normalizedTerm !== '' && customerCpf.includes(normalizedTerm)) ||
        managerName.includes(term)
      );
    });
    
    this.page = 1;
  }

  get paginatedCustomers(): ClienteRelatorio[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredCustomers.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredCustomers.length / this.pageSize);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages;
    const pages: number[] = [];
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
    return pages;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.page = page;
    }
  }

  formatarCpf(cpf: string): string {
    const digits = (cpf || '').replace(/\D/g, '').slice(0, 11);
    if (digits.length !== 11) return cpf || '-';
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9, 11)}`;
  }

  formatarValor(valor: number): string {
    return (valor ?? 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}