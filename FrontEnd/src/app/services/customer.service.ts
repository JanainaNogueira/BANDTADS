import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import { Customer } from '../models/customer.model';
import { Status } from '../models/status-enum.model';
import { environment } from '../../enviroment';

interface BackendCliente {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  telefone?: string;
  salario?: number;
  endereco?: { cidade?: string; estado?: string };
  status:Status;
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
  private readonly clientesApiUrl = `${environment.apiUrl}/clientes`;
  private readonly contasApiUrl = `${environment.apiUrl}/contas`;

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

  obterRelatorioClientes(): Observable<Customer[]> {
    return this.http.get<any[]>(`${this.clientesApiUrl}?filtro=adm_relatorio_clientes`).pipe(
      map(clientes => clientes.map(c => ({
        idCliente: String(c.id),
        cpf: c.cpf,
        name: c.nome,
        email: c.email,
        salary: c.salario,
        numberAccount: c.conta || '-',
        balance: c.saldo ?? 0,
        limit: c.limite ?? 0,
        city: '',
        state: '',
        manager: { cpf: c.gerente?.cpf || '-', name: c.gerente?.nome || '-' },
        status: c.status
      })))
    );
  }

  obterClientesPendentes(): Observable<Customer[]> {
    return this.http
      .get<any[]>(`${this.clientesApiUrl}/status/PENDENTE`)
      .pipe(
        map(clientes =>
          clientes.map(c => ({
            id: c.id,
            idCliente: String(c.id),
            cpf: c.cpf,
            name: c.nome,
            email: c.email,
            salary: c.salario,
            numberAccount: '',
            balance: 0,
            limit: 0,
            city: '',
            state: '',
            manager: { cpf: '', name: '' },
            status: c.status
          }))
        )
      );
  }

  aprovarCliente(id: number): Observable<any> {
    return this.http.post<any>(`${this.clientesApiUrl}/${id}/aprovar`, {});
  }

  rejeitarCliente(id: number,  motivo: string): Observable<any> {
    return this.http.post<any>(`${this.clientesApiUrl}/${id}/rejeitar`, motivo);
  }

  setClienteLogado(cpf: string): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem('bantads_logged_user', cpf.replace(/\D/g, ''));
    }
  }

  getClienteLogado(): Observable<Customer | null> {
    if (typeof window === 'undefined') return of(null);
    const email = localStorage.getItem('email');
    if (!email) return of(null);
    return this.buscarClientePorEmail(email);
  }

  atualizarCliente(cliente: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.clientesApiUrl}/${cliente.id}`, cliente);
  }

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
                  numberAccount: conta?.numeroConta ?? '',
                  balance: conta ? conta.saldo : 0,
                  limit: conta ? conta.limite : 0,
                  city: c.endereco?.cidade ?? '',
                  state: c.endereco?.estado ?? '',
                  manager: { cpf: '', name: '' },
                  status: c.status ?? 'PENDENTE'
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
                  numberAccount: '',
                  balance: 0,
                  limit: 0,
                  city: c.endereco?.cidade ?? '',
                  state: c.endereco?.estado ?? '',
                  manager: { cpf: '', name: '' },
                  status: c.status ?? Status.PENDENTE,
                })
              )
            );
        });

        return forkJoin(observables);
      })
    );
  }
}
