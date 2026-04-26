import { Injectable } from '@angular/core';
import { ENDPOINTS } from '../../../core/api/endpoint';
import { ApiService } from '../../../core/api/api.service';

@Injectable({ providedIn: 'root' })
export class GerenteService {

  constructor(private api: ApiService) {}

  listarTodos() {
    return this.api.get(ENDPOINTS.gerente.listar);
  }

  listar(cpf: string){
    return this.api.get(ENDPOINTS.gerente.listarGerente(cpf))
  }

  criar(gerente: any) {
    return this.api.post(ENDPOINTS.gerente.criar, gerente);
  }

  atualizar(cpf: string, gerente: any) {
    return this.api.put(ENDPOINTS.gerente.atualizar(cpf), gerente);
  }

  deletar(cpf: string) {
    return this.api.delete(ENDPOINTS.gerente.deletar(cpf));
  }
}