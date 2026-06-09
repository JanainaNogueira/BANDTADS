import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../enviroment';

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
  private readonly API_URL = `${environment.apiUrl}/contas`;

  constructor(private http: HttpClient) {}

  depositar(contaId: number | string, valor: number): Observable<void> {
    const numContaId = typeof contaId === 'string' ? parseInt(contaId, 10) : contaId;
    return this.http.post<void>(`${this.API_URL}/${numContaId}/deposito`, {
      contaIdLogada: numContaId,
      valor: valor
    });
  }

  sacar(contaId: number | string, valor: number): Observable<void> {
    const numContaId = typeof contaId === 'string' ? parseInt(contaId, 10) : contaId;
    return this.http.post<void>(`${this.API_URL}/${numContaId}/saque`, {
      contaIdLogada: numContaId,
      valor: valor
    });
  }

  transferir(contaId: number | string, numeroContaDestino: string, valor: number): Observable<void> {
    const numContaId = typeof contaId === 'string' ? parseInt(contaId, 10) : contaId;
    return this.http.post<void>(`${this.API_URL}/${numContaId}/transferencia`, {
      contaIdLogada: numContaId,
      numeroContaDestino: numeroContaDestino,
      valor: valor
    });
  }

  consultarExtrato(contaId: string, inicio: Date, fim: Date): Observable<Transacao[]> {
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
