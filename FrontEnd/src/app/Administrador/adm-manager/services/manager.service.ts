import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ManagerSummary, ManagerStatus } from '../../../models/manager.model';

interface GerenteResponse {
  id: number;
  nome: string;
  email: string;
  telefone: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  private API = 'http://localhost:8080/gerentes';

  constructor(private http: HttpClient) {}

  listar(): Observable<ManagerSummary[]> {
    return this.http.get<GerenteResponse[]>(this.API).pipe(
      map(res => res.map(item => ({
        id: item.id,
        name: item.nome,
        email: item.email,
        phone: item.telefone,
        status: this.mapStatus(item.status)
      })))
    );
  }

  criar(dados: any) {
    return this.http.post(this.API, dados);
  }

  atualizar(id: number, dados: any) {
    return this.http.put(`${this.API}/${id}`, dados);
  }

  deletar(id: number) {
    return this.http.delete(`${this.API}/${id}`);
  }

  private mapStatus(status: string): ManagerStatus {
    return status === 'ATIVO' ? 'active' : 'inactive';
  }
}