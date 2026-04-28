import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MOCK_CUSTOMERS } from '../../assets/mock/customers.mock';
import { Customer } from '../models/costumer.model';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
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
  private readonly STORAGE_KEY = 'bantads_customers';
  private readonly LOGGED_USER_KEY = 'bantads_logged_user';

  private readonly clientesApiUrl = 'http://localhost:8080/clientes';
  private readonly contasApiUrl = 'http://localhost:8081/contas';

  constructor(@Inject(PLATFORM_ID) private platformId: Object, private http: HttpClient) {
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

  getClienteLogado(): Customer | null {
    if (!isPlatformBrowser(this.platformId)) {
      return MOCK_CUSTOMERS.length ? MOCK_CUSTOMERS[0] : null;
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
      return MOCK_CUSTOMERS.find(
        (item) => this.normalizarCpf(item.cpf) === this.normalizarCpf(cpf)
      ) || null;
    }

    const clientes = this.obterTodosClientes();
    const cpfNormalizado = this.normalizarCpf(cpf);

    return clientes.find(
      (item) => this.normalizarCpf(item.cpf) === cpfNormalizado
    ) || null;
  }

  obterTodosClientes(): Customer[] {
    if (!isPlatformBrowser(this.platformId)) {
      return MOCK_CUSTOMERS;
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
      }),
      catchError(() => of(MOCK_CUSTOMERS))
    );
  }

  private normalizarCpf(cpf: string): string {
    return cpf?.replace(/\D/g, '') || '';
  }
}