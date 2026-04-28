import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MOCK_CUSTOMERS } from '../../assets/mock/customers.mock';
import { Customer } from '../models/costumer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly STORAGE_KEY = 'bantads_customers';
  private readonly LOGGED_USER_KEY = 'bantads_logged_user';
  private readonly API_URL = '/api/clientes';

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    // initializeStorage removido para evitar uso de MOCKS
  }

  private initializeStorage(): void {
    // Método desativado
  }

  getClienteLogado(): Customer | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const cpf = localStorage.getItem(this.LOGGED_USER_KEY);

    if (!cpf) {
      const clientes = this.obterTodosClientes();
      if (!clientes.length) return null;

      const primeiro = clientes[0];
      this.setClienteLogado(primeiro.cpf);
      return primeiro;
    }

    return this.buscarClientePorCpf(cpf);
  }

  setClienteLogado(cpf: string): void {
    if (!isPlatformBrowser(this.platformId)) return;
    localStorage.setItem(this.LOGGED_USER_KEY, this.normalizarCpf(cpf));
  }

  buscarClientePorCpf(cpf: string): Customer | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const clientes = this.obterTodosClientes();
    const cpfNormalizado = this.normalizarCpf(cpf);

    return clientes.find(
      (item) => this.normalizarCpf(item.cpf) === cpfNormalizado
    ) || null;
  }

  obterTodosClientes(): Customer[] {
    if (!isPlatformBrowser(this.platformId)) {
      return [];
    }

    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
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

  atualizarCliente(clienteAtualizado: Customer): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const clientes = this.obterTodosClientes();

    const index = clientes.findIndex(
      (c) => this.normalizarCpf(c.cpf) === this.normalizarCpf(clienteAtualizado.cpf)
    );

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
    const cpfNormalizado = this.normalizarCpf(cpf);

    const filtrados = clientes.filter(
      (c) => this.normalizarCpf(c.cpf) !== cpfNormalizado
    );

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(filtrados));
  }

  limparStorage(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    localStorage.removeItem(this.STORAGE_KEY);
    localStorage.removeItem(this.LOGGED_USER_KEY);
  }

  private normalizarCpf(cpf: string): string {
    return cpf?.replace(/\D/g, '') || '';
  }

  obterClientesPendentes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/status/PENDENTE`);
  }

  aprovarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/${id}/aprovar`, {});
  }

  rejeitarCliente(id: number, motivo: string = ''): Observable<any> {
    // Caso a API aceite um DTO com motivo no futuro, enviar no body
    return this.http.post<any>(`${this.API_URL}/${id}/rejeitar`, { motivo });
  }
}