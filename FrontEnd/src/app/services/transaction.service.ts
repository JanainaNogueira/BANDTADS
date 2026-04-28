import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Transacao {
  id?: number;
  dataHora?: Date;
  tipo: 'DEPOSITO' | 'SAQUE' | 'TRANSFERENCIA';
  valor: number;
  contaId?: number;
  contaOrigem?: number;
  contaDestino?: number;
  clienteOrigemId?: number;
  clienteDestinoId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly API_URL = '/api/contas';

  constructor(private http: HttpClient) {}

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
    const dataInicioYMD = inicio.toISOString().split('T')[0];
    const dataFimYMD = fim.toISOString().split('T')[0];
    
    return this.http.get<Transacao[]>(`${this.API_URL}/${contaId}/extrato`, {
      params: {
        dataInicio: dataInicioYMD,
        dataFim: dataFimYMD
      }
    });
  }
}
