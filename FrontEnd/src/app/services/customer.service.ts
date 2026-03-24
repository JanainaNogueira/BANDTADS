import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MOCK_CUSTOMERS } from '../../assets/mock/customers.mock';
import { Customer } from '../models/costumer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly STORAGE_KEY = 'bantads_customers';

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeStorage();
  }

//  Quando tiver API vou remover essa inicialização

  private initializeStorage(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (!stored) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(MOCK_CUSTOMERS));
    }
  }

  buscarClientePorCpf(cpf: string): Customer | null {
    if (!isPlatformBrowser(this.platformId)) {
      return MOCK_CUSTOMERS.find((item) => this.normalizarCpf(item.cpf) === this.normalizarCpf(cpf)) || null;
    }
    const clientes = this.obterTodosClientes();
    const cpfNormalizado = this.normalizarCpf(cpf);
    return clientes.find((item) => this.normalizarCpf(item.cpf) === cpfNormalizado) || null;
  }

  obterTodosClientes(): Customer[] {
    if (!isPlatformBrowser(this.platformId)) {
      return MOCK_CUSTOMERS;
    }
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }

  adicionarCliente(cliente: Customer): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const clientes = this.obterTodosClientes();
    clientes.push(cliente);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(clientes));
  }

  salvarClientes(clientes: Customer[]): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(clientes));
  }

  atualizarCliente(cpf: string, clienteAtualizado: Customer): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const clientes = this.obterTodosClientes();
    const index = clientes.findIndex((c) => this.normalizarCpf(c.cpf) === this.normalizarCpf(cpf));
    if (index !== -1) {
      clientes[index] = clienteAtualizado;
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(clientes));
    }
  }

  removerCliente(cpf: string): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const clientes = this.obterTodosClientes();
    const filtrados = clientes.filter((c) => this.normalizarCpf(c.cpf) !== this.normalizarCpf(cpf));
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(filtrados));
  }

  limparStorage(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private normalizarCpf(cpf: string): string {
    return cpf.replace(/\D/g, '');
  }
}
