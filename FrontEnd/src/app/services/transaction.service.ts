import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { MOCK_TRANSACTION_LIST } from '../../assets/mock/customers.mock';

export interface Transacao {
  id: number;
  dataHora: Date;
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

  private transactionsSubject = new BehaviorSubject<Transacao[]>([]);
  transactions$ = this.transactionsSubject.asObservable();

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeStorage();

    const lista = this.getAllTransactions();
    this.transactionsSubject.next(lista);
  }

  private initializeStorage(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const stored = localStorage.getItem(this.STORAGE_KEY);

    if (!stored) {
      localStorage.setItem(
        this.STORAGE_KEY,
        JSON.stringify(MOCK_TRANSACTION_LIST)
      );
    }
  }

  getAllTransactions(): Transacao[] {
    if (!isPlatformBrowser(this.platformId)) {
      return this.toDateList(MOCK_TRANSACTION_LIST as Transacao[]);
    }

    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      const parsed = raw ? (JSON.parse(raw) as Transacao[]) : [];

      return this.toDateList(parsed);
    } catch {
      return this.toDateList(MOCK_TRANSACTION_LIST as Transacao[]);
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

    const nextId = all.length
      ? Math.max(...all.map((item) => item.id)) + 1
      : 1;

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


  transferir(
    contaOrigem: number,
    contaDestino: number,
    valor: number
  ): void {
    if (contaOrigem === contaDestino) return;
    if (valor <= 0) return;

    // saída
    this.addTransaction({
      tipo: 'TRANSFERENCIA',
      valor: -valor,
      contaOrigem,
      contaDestino,
    });

    // entrada
    this.addTransaction({
      tipo: 'TRANSFERENCIA',
      valor: valor,
      contaOrigem: contaDestino,
      contaDestino: contaOrigem,
    });
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
        dataHora: new Date(item.dataHora),
      }))
      .sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());
  }
}