import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../enviroment';

@Injectable({ providedIn: 'root' })
export class CompositionService {
  private API = `${environment.apiUrl}/gerentes?filtro=dashboard`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<any[]> {
    return this.http.get<any[]>(this.API).pipe(
      map(data => data.map(item => ({
        name: item.gerente?.nome,
        totalClients: item.clientes?.length ?? 0,
        totalPositive: item.saldo_positivo ?? 0,
        totalNegative: Math.abs(item.saldo_negativo ?? 0)
      })))
    );
  }

}
