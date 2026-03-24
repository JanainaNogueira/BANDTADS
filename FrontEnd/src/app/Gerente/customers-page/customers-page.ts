import { Component, OnInit } from '@angular/core';
import { CustomersList } from '../../components/customers-list/customers-list';
import { ManagerTopPanel } from '../../components/manager-top-panel/manager-top-panel';
import { Menu } from '../../components/menu/menu';
import { CustomerService } from '../../services/customer.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TopClientes } from './top-clientes/top-clientes';
import { ActivatedRoute, Router } from '@angular/router';
import { ManagerConsultarCliente } from '../manager-consultar-cliente/manager-consultar-cliente';
import { Customer } from '../../models/costumer.model';

@Component({
  selector: 'app-customers-page',
  imports: [Menu, ManagerTopPanel, CustomersList, FormsModule, CommonModule, TopClientes, ManagerConsultarCliente],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit {
  customers: Customer[] = [];
  searchTerm: string = '';
  page:number=1;

  constructor(
    private readonly customerService: CustomerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.customers = this.customerService.obterTodosClientes();
    this.route.url.subscribe((segments) => {
      const path = segments[0]?.path;
      this.page = path === 'gerente-consultar-cliente' ? 2 : 1;
    });
  }

  get filteredCustomers(): Customer[] {
    if (!this.searchTerm.trim()) {
      return this.customers;
    }

    const searchLower = this.searchTerm.toLowerCase();
    const searchNumbers = this.searchTerm.replace(/\D/g, '');

    return this.customers.filter(customer => {
      const cpfNumbers = customer.cpf.replace(/\D/g, '');
      const nameMatch = customer.name.toLowerCase().includes(searchLower);
      const cpfMatch = cpfNumbers.includes(searchNumbers);
      
      return nameMatch || cpfMatch;
    });
  }

  switchPage(page:number){
    this.page = page;

    if (page === 1) {
      this.router.navigate(['gerente-clientes']);
      return;
    }

    if (page === 2) {
      this.router.navigate(['gerente-consultar-cliente']);
    }
  }

}
