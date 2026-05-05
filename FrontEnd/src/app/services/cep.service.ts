import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ViaCepResponse {
  logradouro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

@Injectable({
  providedIn: 'root'
})

export class CepService {

  constructor(private http: HttpClient) {}

  buscarCep(cep: string): Observable<ViaCepResponse> {
    return this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${cep}/json`);
  }
}