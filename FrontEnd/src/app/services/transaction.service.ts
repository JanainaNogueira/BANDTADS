import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { MOCK_TRANSACTION_LIST } from '../../assets/mock/customers.mock';

export interface Transacao {
  id?: number;
  dataHora?: Date;
  tipo: 'DEPOSITO' | 'SAQUE' | 'TRANSFERENCIA';
  valor: number;
  contaOrigem: number;
  contaDestino?: number;
}

export interface TransacaoExtrato extends Transacao {
  isEntrada: boolean;
  nomeOutro: string;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly STORAGE_KEY = 'bantads_transactions';
  private readonly API_URL = '/api/contas'; // Utilizar proxy/gateway

  private transactionsSubject = new BehaviorSubject<Transacao[]>([]);
  transactions$ = this.transactionsSubject.asObservable();

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    // initializeStorage removido para evitar uso de MOCKS
    
    const lista = this.getAllTransactions();
    this.transactionsSubject.next(lista);
  }

  depositar(contaId: number, valor: number): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/${contaId}/deposito`, {
      contaIdLogada: contaId,
      valor: valor
    });
  }

  sacar(contaId: number, valor: number): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/${contaId}/saque`, {
      contaIdLogada: contaId,
      valor: valor
    });
  }

  transferir(contaId: number, numeroContaDestino: string, valor: number): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/${contaId}/transferencia`, {
      contaIdLogada: contaId,
      numeroContaDestino: numeroContaDestino,
      valor: valor
    });
  }

  consultarExtrato(contaId: number, inicio: Date, fim: Date): Observable<Transacao[]> {
    const dataInicioFormatoYMD = inicio.toISOString().split('T')[0];
    const dataFimFormatoYMD = fim.toISOString().split('T')[0];
    
    return this.http.get<Transacao[]>(`${this.API_URL}/${contaId}/extrato`, {
      params: {
        dataInicio: dataInicioFormatoYMD,
        dataFim: dataFimFormatoYMD
      }
    });
  }

  private initializeStorage(): void {
    // Método desativado
  }

  getAllTransactions(): Transacao[] {
    if (!isPlatformBrowser(this.platformId)) {
      return [];
    }

    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      const parsed = raw ? (JSON.parse(raw) as Transacao[]) : [];

      return this.toDateList(parsed);
    } catch {
      return [];
    }
  }

  getTransactionsByAccount(conta: number): Transacao[] {
    return this.getAllTransactions().filter(
      (t) => t.contaOrigem === conta || t.contaDestino === conta
    );
  }

  getRecentTransactions(limit = 3): Transacao[] {
    return this.getAllTransactions().slice(0, limit);
  }

  addTransaction(data: Omit<Transacao, 'id' | 'dataHora'>): void {

    const all = this.getAllTransactions();
    const ids = all.map((item) => item.id).filter((id): id is number => typeof id === 'number');
    const nextId = ids.length ? Math.max(...ids) + 1 : 1;

    const newTransaction: Transacao = {
      ...data,
      id: nextId,
      dataHora: new Date(),
    };

    const updatedList = [newTransaction, ...all];

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(
        this.STORAGE_KEY,
        JSON.stringify(updatedList)
      );
    }

    this.transactionsSubject.next(updatedList);
  }



  isEntrada(t: Transacao, conta: number): boolean {
    if (t.tipo === 'DEPOSITO') return true;
    if (t.tipo === 'SAQUE') return false;

    return t.contaDestino === conta;
  }

  private toDateList(list: Transacao[]): Transacao[] {
    return list
      .map((item) => ({
        ...item,
        dataHora: item.dataHora ? new Date(item.dataHora) : new Date(),
      }))
      .sort((a, b) => {
        const timeA = a.dataHora ? a.dataHora.getTime() : 0;
        const timeB = b.dataHora ? b.dataHora.getTime() : 0;
        return timeB - timeA;
      });
  }
}