import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoginService {
  getTipo(): string {
    if (typeof window !== 'undefined') {
      return (localStorage.getItem('tipoUsuario') || '').toLowerCase();
    }
    return '';
  }
}
