import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  access_token: string;
  token_type: string;
  tipo: string;
  usuario: any;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly authApiUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  login(login: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authApiUrl}/login`, { login, senha });
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('tipoUsuario');
      localStorage.removeItem('email');
      localStorage.removeItem('nome');
    }
  }

  getTipo(): string {
    if (typeof window !== 'undefined') {
      return (localStorage.getItem('tipoUsuario') || '').toLowerCase();
    }
    return '';
  }

  isLoggedIn(): boolean {
    if (typeof window !== 'undefined') {
      return !!localStorage.getItem('token');
    }
    return false;
  }
}
