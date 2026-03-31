import { CurrencyPipe} from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CpfPipe } from '../../../../pipes/cpf.pipe';
import { ModalRecusar } from '../modal-recusar/modal-recusar';
import { Status } from '../../../../models/status-enum.model';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

export interface Manager {
  name: string;
  cpf: string;
}

export interface Customer {
  cpf: string;
  name: string;
  email: string;
  salary: number;
  status: Status;
  password?: string;
}

@Component({
  selector: 'app-customers-home',
  imports: [CurrencyPipe, MatIconModule, CpfPipe, ModalRecusar, MatSnackBarModule],
  templateUrl: './customers-home.html',
  styleUrl: './customers-home.css',
})

export class CustomersHome {

  constructor(private snackBar: MatSnackBar) {}

  @Input() customers: Customer[] = [];

  sortDirection: 'asc' | 'desc' = 'asc';

  currentPage = 1;
  itemsPerPage = 5; 
  cliente = null;

  selectedCustomer: Customer = {
    cpf: '',
    name: '',
    email: '',
    salary: 0,
    status: Status.PENDENTE,
    password: ''
  };

  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.currentPage = 1;
  }

  get filteredCustomers() {
    return this.customers.filter(c => c.status === Status.PENDENTE);
  }

  get sortedCustomers() {
    return [...this.filteredCustomers].sort((a, b) => {
      const result = a.name.localeCompare(b.name);

      return this.sortDirection === 'asc' ? result : -result;
    });
  }
  
  get totalPages() {
    return Math.ceil(this.sortedCustomers.length / this.itemsPerPage);
  }
  
  get paginatedCustomers() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;

    return this.sortedCustomers.slice(start, end);
  }

  modalAberto = false;

  abrirModal(customer: Customer) {
    this.selectedCustomer = customer;
    this.modalAberto = true;
  }

  fecharModal() {
    this.modalAberto = false;
  }

  recusarCliente(cpf: string) {
    this.customers = this.customers.filter(c => c.cpf !== cpf);
    this.fecharModal();
  }

  aprovarCadastro(cliente: any) {

    const customer = this.customers.find(c => c.cpf === cliente.cpf);

    if (customer) {
      const senhaGerada = this.gerarSenha();

      customer.status = Status.APROVADO;
      customer.password = senhaGerada;

      this.enviarEmail(customer.email, senhaGerada);
    }

    this.snackBar.open('Enviando e-mail com as credenciais...', 'Fechar', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['text-white', 'rounded-3xl']
    });

    setTimeout(() => {
      this.snackBar.open('Cliente aprovado com sucesso!', 'Fechar', {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'bottom',
        panelClass: ['text-white', 'rounded-3xl']
      });
    }, 3500);
  }

  gerarSenha(tamanho: number = 4): string {
    const caracteres = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%';
    let senha = '';

    for (let i = 0; i < tamanho; i++) {
      senha += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
    }

    return senha;
  }

  enviarEmail(email: string, senha: string) {
    console.log('--- EMAIL ENVIADO ---');
    console.log(`Para: ${email}`);
    console.log(`Sua conta foi criada!`);
    console.log(`Login: ${email}`);
    console.log(`Senha: ${senha}`);
  }
}
