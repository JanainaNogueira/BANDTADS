import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MOCK_TRANSACTION_LIST } from '../../assets/mock/customers.mock';

export type TipoOperacao = 'Depósito' | 'Saque' | 'Transferência';

export interface Transacao {
  id: number;
  dataHora: Date;
  operacao: TipoOperacao;
  nomeCliente: string;
  valor: number;
  isEntrada: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly STORAGE_KEY = 'bantads_transactions';

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeStorage();
  }

  private initializeStorage(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (!stored) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(MOCK_TRANSACTION_LIST));
    }
  }

  getAllTransactions(): Transacao[] {
    if (!isPlatformBrowser(this.platformId)) {
      return this.toDateList(MOCK_TRANSACTION_LIST as Transacao[]);
    }

    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      const parsed = raw ? (JSON.parse(raw) as Transacao[]) : [];
      return this.toDateList(parsed).sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());
    } catch {
      return this.toDateList(MOCK_TRANSACTION_LIST as Transacao[]);
    }
  }

  getRecentTransactions(limit = 3): Transacao[] {
    return this.getAllTransactions().slice(0, limit);
  }

  addTransaction(data: Omit<Transacao, 'id' | 'dataHora'>): void {
    const all = this.getAllTransactions();
    const nextId = all.length ? Math.max(...all.map((item) => item.id)) + 1 : 1;

    const newTransaction: Transacao = {
      ...data,
      id: nextId,
      dataHora: new Date(),
    };

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(
        this.STORAGE_KEY,
        JSON.stringify([newTransaction, ...all])
      );
    }
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
