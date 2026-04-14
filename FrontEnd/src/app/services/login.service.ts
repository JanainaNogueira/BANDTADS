import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  id?: string;
  login: string;
  tipo: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly authApiUrl = 'http://localhost:5000/auth';

  constructor(private http: HttpClient) {}

  login(login: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authApiUrl}/login`, { login, senha });
  }

  getTipo(): string {
    if (typeof window !== 'undefined') {
      return (localStorage.getItem('tipoUsuario') || '').toLowerCase();
    }
    return '';
  }
}
