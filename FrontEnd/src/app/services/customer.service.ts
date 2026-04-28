import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/costumer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly API_URL = '/api/clientes';

  constructor(private http: HttpClient) {}

  buscarClientePorCpf(cpf: string): Observable<Customer> {
    const cpfLimpo = cpf.replace(/\D/g, '');
    return this.http.get<Customer>(`${this.API_URL}/cpf/${cpfLimpo}`);
  }

  buscarClientePorEmail(email: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.API_URL}/email/${email}`);
  }

  obterTodosClientes(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.API_URL);
  }

  obterClientesPendentes(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.API_URL}/status/PENDENTE`);
  }

  aprovarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/${id}/aprovar`, {});
  }

  rejeitarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/${id}/rejeitar`, {});
  }

  setClienteLogado(cpf: string): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem('bantads_logged_user', cpf.replace(/\D/g, ''));
    }
  }

  getClienteLogado(): Observable<Customer | null> {
    if (typeof window === 'undefined') return new Observable(sub => sub.next(null));
    const email = localStorage.getItem('email');
    if (!email) return new Observable(sub => sub.next(null));
    return this.buscarClientePorEmail(email);
  }

  atualizarCliente(cliente: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.API_URL}/${cliente.id}`, cliente);
  }
}
