import { Injectable } from '@angular/core';
import { ENDPOINTS } from '../../../core/api/endpoint';
import { ApiService } from '../../../core/api/api.service';

@Injectable({ providedIn: 'root' })
export class GerenteService {

  constructor(private api: ApiService) {}

  listarTodos() {
    return this.api.get(ENDPOINTS.gerente.listar);
  }

  listar(id: number){
    return this.api.get(ENDPOINTS.gerente.listarGerente(id))
  }

  criar(gerente: any) {
    return this.api.post(ENDPOINTS.gerente.criar, gerente);
  }

  atualizar(id: number, gerente: any) {
    return this.api.put(ENDPOINTS.gerente.atualizar(id), gerente);
  }

  deletar(id: number) {
    return this.api.delete(ENDPOINTS.gerente.deletar(id));
  }
}