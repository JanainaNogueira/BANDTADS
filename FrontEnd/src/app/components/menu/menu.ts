import { Component, OnInit } from '@angular/core';
import { MOCK_CUSTOMERS, MOCK_LOGIN_USER } from '../../../assets/mock/customers.mock';
import { MOCK_MANAGERS, MOCK_MANAGERS_LIST } from '../../../assets/mock/managers.mock';
import { MOCK_ADMINS } from '../../../assets/mock/admin.mock';
import { Customer } from '../../models/costumer.model';
import { ManagerCreateEdit, ManagerSummary } from '../../models/manager.model';
import { Admin } from '../../models/admin.model';
import { LoginService } from '../../services/login.service';
import { CustomerService } from '../../services/customer.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MenuActions } from './components/menu-actions/menu-actions';

@Component({
  selector: 'app-menu',
  imports: [CommonModule],
  templateUrl: './menu.html',
  styleUrl: './menu.css',
})
export class Menu implements OnInit{
  cliente = MOCK_CUSTOMERS;
    gestores = MOCK_MANAGERS;
    admin = MOCK_ADMINS;
  
    email: string = '';
    tipo: string = "";
    loginCliente:Customer | null = null;
    loginGestor:ManagerCreateEdit | null = null;
    loginAdmin:Admin | null = null;
    constructor(private auth: LoginService, private router : Router){}
    ngOnInit() {
      this.email = localStorage.getItem('email') || '';
      this.tipo = this.auth.getTipo();
      if(this.tipo == "cliente"){
        this.loginCliente = this.cliente.find(l =>l.email == this.email) || null;
      }else if(this.tipo == "gerente"){
        this.loginGestor = this.gestores.find(l =>l.email == this.email) || null;
      }else{
        this.loginAdmin = this.admin.find(l =>l.email == this.email) || null;
      }
      
  
    }
  
  
  getInitials(name: string): string {
    return name
      .split(' ')
      .map(n => n[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  menuAberto = false;

  toggleMenu() {
    this.menuAberto = !this.menuAberto;
    console.log('Menu aberto:', this.menuAberto);
  }

  logout(event: Event) {
    event.stopPropagation();

    localStorage.removeItem('bantads_logged_user');

    this.router.navigate(['/login']);
  }
}
