import { Injectable } from '@angular/core';
import { Customer } from '../components/customers-list/customers-list';
import { MOCK_CUSTOMERS } from '../../assets/mock/customers.mock';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly STORAGE_KEY = 'bantads_customers';

  constructor() {
    this.initializeStorage();
  }

//  Quando tiver API vou remover essa inicialização

  private initializeStorage(): void {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (!stored) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(MOCK_CUSTOMERS));
    }
  }

  buscarClientePorCpf(cpf: string): Customer | null {
    const clientes = this.obterTodosClientes();
    const cpfNormalizado = this.normalizarCpf(cpf);
    return clientes.find((item) => this.normalizarCpf(item.cpf) === cpfNormalizado) || null;
  }

  obterTodosClientes(): Customer[] {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }

  adicionarCliente(cliente: Customer): void {
    const clientes = this.obterTodosClientes();
    clientes.push(cliente);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(clientes));
  }

  atualizarCliente(cpf: string, clienteAtualizado: Customer): void {
    const clientes = this.obterTodosClientes();
    const index = clientes.findIndex((c) => this.normalizarCpf(c.cpf) === this.normalizarCpf(cpf));
    if (index !== -1) {
      clientes[index] = clienteAtualizado;
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(clientes));
    }
  }

  removerCliente(cpf: string): void {
    const clientes = this.obterTodosClientes();
    const filtrados = clientes.filter((c) => this.normalizarCpf(c.cpf) !== this.normalizarCpf(cpf));
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(filtrados));
  }

  limparStorage(): void {
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private normalizarCpf(cpf: string): string {
    return cpf.replace(/\D/g, '');
  }
}
