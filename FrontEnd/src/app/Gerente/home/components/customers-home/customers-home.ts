import { CurrencyPipe} from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CpfPipe } from '../../../../pipes/cpf.pipe';
import { ModalRecusar } from '../modal-recusar/modal-recusar';
import { Status } from '../../../../models/status-enum.model';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CustomerService } from '../../../../services/customer.service';

export interface Manager {
  name: string;
  cpf: string;
}

export interface Customer {
  id?: number;
  cpf: string;
  nome?: string;
  name?: string;
  email: string;
  salario?: number;
  salary?: number;
  status: Status | string;
  password?: string;
}

@Component({
  selector: 'app-customers-home',
  imports: [CurrencyPipe, MatIconModule, CpfPipe, ModalRecusar, MatSnackBarModule],
  templateUrl: './customers-home.html',
  styleUrl: './customers-home.css',
})

export class CustomersHome {

  constructor(private snackBar: MatSnackBar, private customerService: CustomerService) {}

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
    return this.customers.filter(c => c.status === Status.PENDENTE || c.status === 'PENDENTE');
  }

  get sortedCustomers() {
    return [...this.filteredCustomers].sort((a, b) => {
      const nameA = a.nome || a.name || '';
      const nameB = b.nome || b.name || '';
      const result = nameA.localeCompare(nameB);

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
    const customer = this.customers.find(c => c.cpf === cpf);
    if (!customer) return;

    if (customer.id) {
      this.customerService.rejeitarCliente(customer.id).subscribe({
        next: () => {
           this.customers = this.customers.filter(c => c.cpf !== cpf);
           this.fecharModal();
        },
        error: (err) => console.error("Erro ao recusar cliente", err)
      });
    } else {
      console.warn("Cliente sem ID para recusar via API");
    }
  }

  aprovarCadastro(cliente: Customer) {
    const customer = this.customers.find(c => c.cpf === cliente.cpf);
    if (!customer) return;

    const email = customer.email;
    const senhaGerada = this.gerarSenha();

    if (customer.id) {
      // Chama API Back-End REAL
      this.customerService.aprovarCliente(customer.id).subscribe({
        next: (res) => {
          // Remove da lista de pendentes da tela
          this.customers = this.customers.filter(c => c.cpf !== cliente.cpf);
          
          this.enviarEmail(email, senhaGerada);
          this.exibirSnackbarParaAprovacao();
        },
        error: (err) => console.error("Erro ao aprovar cliente", err)
      });
    } else {
      console.warn("Cliente sem ID para aprovar via API");
    }
  }

  private exibirSnackbarParaAprovacao() {
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
