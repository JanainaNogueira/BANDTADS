import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_CONFIG } from './api.config';

@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  get<T>(url: string) {
    return this.http.get<T>(API_CONFIG.baseUrl + url);
  }

  post<T>(url: string, body: any) {
    return this.http.post<T>(API_CONFIG.baseUrl + url, body);
  }

  put<T>(url: string, body: any) {
    return this.http.put<T>(API_CONFIG.baseUrl + url, body);
  }

  delete<T>(url: string) {
    return this.http.delete<T>(API_CONFIG.baseUrl + url);
  }
}