import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CompositionService {
  private API = 'http://localhost:8080/gerentes?filtro=dashboard';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<any[]> {
    return this.http.get<any[]>(this.API);
  }

}
