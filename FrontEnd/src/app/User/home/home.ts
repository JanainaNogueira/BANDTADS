import { Component, OnInit } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Customer } from '../../models/costumer.model';
import { CustomerService } from '../../services/customer.service';
import { TransactionService, Transacao } from '../../services/transaction.service';
import { Operacoes } from './components/operacoes/operacoes';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [Menu, CommonModule, MatIconModule, MatDialogModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  login: Customer | null = null;
  transactions: any[] = [];

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private customerService: CustomerService,
    private transactionService: TransactionService
  ) {}

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    const email = localStorage.getItem('email');
    if (email) {
      this.customerService.buscarClientePorEmail(email).subscribe({
        next: (cliente) => {
          this.login = cliente;
          this.carregarUltimasTransacoes();
        },
        error: (err) => console.error('Erro ao carregar dados do cliente', err)
      });
    }
  }

  carregarUltimasTransacoes() {
    if (!this.login || !this.login.numberAccount) return;

    const hoje = new Date();
    const inicio = new Date();
    inicio.setDate(hoje.getDate() - 30);

    forkJoin({
      extrato: this.transactionService.consultarExtrato(this.login.numberAccount, inicio, hoje),
      clientes: this.customerService.obterTodosClientes()
    }).subscribe({
      next: (resp) => {
        this.transactions = resp.extrato
          .slice(0, 3)
          .map(t => {
            const isEntrada = t.valor > 0 || t.tipo === 'DEPOSITO' || (t.tipo === 'TRANSFERENCIA' && t.contaDestino === this.login?.numberAccount);
            let nomeOutro = '';

            if (t.tipo === 'TRANSFERENCIA') {
              const outroId = isEntrada ? t.clienteOrigemId : t.clienteDestinoId;
              const outro = resp.clientes.find(c => c.id === outroId);
              nomeOutro = outro ? (outro.name || outro.nome || '') : 'Transferência';
            } else {
              nomeOutro = t.tipo === 'DEPOSITO' ? 'Depósito' : 'Saque';
            }

            return {
              ...t,
              isEntrada,
              nomeOutro,
              operacao: t.tipo
            };
          });
      }
    });
  }

  abrirOperacoes(tabInicial: number): void {
    if (!this.login) return;

    const ref = this.dialog.open(Operacoes, {
      data: { tabInicial, cliente: this.login },
      width: '760px',
      maxWidth: '96vw'
    });

    ref.afterClosed().subscribe(() => this.carregarDados());
  }

  irParaExtrato() {
    this.router.navigate(['/bank-statement']);
  }

  get balanceClass(): string {
    return (this.login?.balance ?? 0) < 0 ? 'text-red-500' : 'text-[#FFFFFF]';
  }

  get formattedBalance(): string {
    const balance = this.login?.balance ?? 0;
    return balance.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}
