import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Customer } from '../../models/customer.model';
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
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    const email = localStorage.getItem('email');
    if (email) {
      this.customerService.buscarClientePorEmail(email).subscribe({
        next: (cliente) => {
          console.log('Cliente carregado no home:', cliente);
          this.login = cliente;
          this.cdr.markForCheck();
          this.carregarUltimasTransacoes();
        },
        error: (err) => console.error('Erro ao carregar dados do cliente', err)
      });
    }
  }

  carregarUltimasTransacoes() {
    if (!this.login) {
      console.warn('Login não carregado');
      return;
    }

    // Se não tiver numberAccount, tentar buscar da primeira conta
    let contaNumeroConta = this.login.numberAccount;
    if (!contaNumeroConta && this.login.accounts && this.login.accounts.length > 0) {
      contaNumeroConta = this.login.accounts[0].numeroConta;
      console.log('numeroConta obtido de accounts:', contaNumeroConta);
    }

    if (!contaNumeroConta) {
      console.warn('Nenhuma conta encontrada para carregar transações');
      return;
    }

    const hoje = new Date();
    const inicio = new Date();
    inicio.setDate(hoje.getDate() - 30);

    forkJoin({
      extrato: this.transactionService.consultarExtrato(contaNumeroConta, inicio, hoje),
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
      },
      error: (err) => console.error('Erro ao carregar transações:', err)
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
