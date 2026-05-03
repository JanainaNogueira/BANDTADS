import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin } from 'rxjs';
import { Customer } from '../models/customer.model';
import { map, catchError, switchMap } from 'rxjs/operators';

interface BackendCliente {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  telefone?: string;
  salario?: number;
  endereco?: { cidade?: string; estado?: string };
}

interface LerContaDTO {
  contaId: number;
  clienteId: number;
  numeroConta: string;
  dataCriacao: string;
  saldo: number;
  limite: number;
  gerenteId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly clientesApiUrl = '/api/clientes';
  private readonly contasApiUrl = '/api/contas';

  constructor(private http: HttpClient) { }

  criarCliente(dados: any): Observable<any> {
    return this.http.post(this.clientesApiUrl, dados);
  }

  buscarClientePorCpf(cpf: string): Observable<Customer> {
    const cpfLimpo = cpf.replace(/\D/g, '');
    return this.http.get<Customer>(`${this.clientesApiUrl}/cpf/${cpfLimpo}`);
  }

  buscarClientePorEmail(email: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.clientesApiUrl}/email/${email}`);
  }

  obterTodosClientes(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.clientesApiUrl);
  }

  obterClientesPendentes(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.clientesApiUrl}/status/PENDENTE`);
  }

  aprovarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.clientesApiUrl}/${id}/aprovar`, {});
  }

  rejeitarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.clientesApiUrl}/${id}/rejeitar`, {});
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
    return this.http.put<Customer>(`${this.clientesApiUrl}/${cliente.id}`, cliente);
  }

  // consultam o back 
  obterTodosClientesApi(): Observable<Customer[]> {

    return this.http.get<BackendCliente[]>(this.clientesApiUrl).pipe(
      switchMap((clientes) => {
        if (!clientes || clientes.length === 0) return of([]);

        const observables = clientes.map((c) => {
          return this.http
            .get<LerContaDTO[]>(`${this.contasApiUrl}/cliente/${c.id}`)
            .pipe(
              map((contas) => {
                const conta = (contas && contas.length) ? contas[0] : null;
                const customer: Customer = {
                  idCliente: String(c.id),
                  cpf: c.cpf,
                  name: c.nome,
                  email: c.email,
                  salary: c.salario ?? 0,
                  numberAccount: conta ? Number(conta.numeroConta) : 0,
                  balance: conta ? conta.saldo : 0,
                  limit: conta ? conta.limite : 0,
                  city: c.endereco?.cidade ?? '',
                  state: c.endereco?.estado ?? '',
                  manager: { cpf: '', name: '' },
                  status: null as any,
                };

                return customer;
              }),
              catchError(() =>
                of({
                  idCliente: String(c.id),
                  cpf: c.cpf,
                  name: c.nome,
                  email: c.email,
                  salary: c.salario ?? 0,
                  numberAccount: 0,
                  balance: 0,
                  limit: 0,
                  city: c.endereco?.cidade ?? '',
                  state: c.endereco?.estado ?? '',
                  manager: { cpf: '', name: '' },
                  status: null as any,
                })
              )
            );
        });

        return forkJoin(observables);
      })
    );
  }
}
