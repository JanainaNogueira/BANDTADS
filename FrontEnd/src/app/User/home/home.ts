import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Operacoes } from '../../components/operacoes/operacoes';
import { Router } from '@angular/router';
import { Customer } from '../../models/costumer.model';
import { CustomerService } from '../../services/customer.service';
import { Transacao, TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-home',
  imports: [Menu, CommonModule, MatIconModule, MatDialogModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(
    private dialog: MatDialog,
    private router: Router,
    private customerService: CustomerService,
    private transactionService: TransactionService
  ) {}

  transactions: any[] = [];

  email: string = '';
  tipo: string = "";
  login: Customer | null = null;

  ngOnInit() {
    this.carregarUsuarioLogado();
    this.carregarTransacoes();

    this.transactionService.transactions$.subscribe(() => {
      this.carregarTransacoes();
    });

    this.transactionService.transactions$.subscribe(lista => {

      if (!this.login) return;

      const clientes = this.customerService.obterTodosClientes();

      this.transactions = lista
        .filter(t =>
          t.contaOrigem === this.login?.numberAccount ||
          t.contaDestino === this.login?.numberAccount
        )
        .slice(0, 3)
        .map(t => {

          const isEntrada = this.isEntrada(t);

          let nomeDestinatario = '';

          if (t.tipo === 'TRANSFERENCIA') {
            if (isEntrada) {
              const origem = clientes.find(c => c.numberAccount === t.contaOrigem);
              nomeDestinatario = origem?.name || '';
            } else {
              const destino = clientes.find(c => c.numberAccount === t.contaDestino);
              nomeDestinatario = destino?.name || '';
            }
          }

          return {
            ...t,
            operacao: this.getOperacao(t),
            isEntrada,
            nomeDestinatario
          };
        });
    });
  }

  private isEntrada(t: Transacao): boolean {
    if (!this.login) return false;

    if (t.tipo === 'DEPOSITO') return true;
    if (t.tipo === 'SAQUE') return false;

    return t.contaDestino === this.login.numberAccount;
  }

  private getOperacao(t: Transacao): string {
    switch (t.tipo) {
      case 'DEPOSITO':
        return 'Depósito';
      case 'SAQUE':
        return 'Saque';
      case 'TRANSFERENCIA':
        return 'Transferência';
      default:
        return '';
    }
  }

  private carregarUsuarioLogado(): void {
    if (typeof localStorage === 'undefined') return;

    this.email = localStorage.getItem('email') || '';
    this.tipo = localStorage.getItem('tipoUsuario') || "";

    if (this.tipo == "cliente") {
      this.login = this.customerService
        .obterTodosClientes()
        .find((cliente) => cliente.email == this.email) || null;
    }
  }

  abrirOperacoes(tabInicial: number): void {
    if (!this.login) {
      console.error('Cliente não carregado');
      return;
    }

    const ref = this.dialog.open(Operacoes, {
      data: {
        tabInicial,
        cliente: this.login
      },
      width: '760px',
      maxWidth: '96vw'
    });

    ref.afterClosed().subscribe(() => {
      this.carregarUsuarioLogado();
      this.carregarTransacoes();
    });
  }

  irParaExtrato(): void {
    this.router.navigate(['/bank-statement']);
  }

  private carregarTransacoes() {
  if (!this.login) return;

  const lista = this.transactionService.getAllTransactions();
  const clientes = this.customerService.obterTodosClientes();

  this.transactions = lista
    .filter(t =>{
      const minhaConta = this.login!.numberAccount;

    if (t.tipo === 'TRANSFERENCIA') {
      // evita duplicidade
      return (
        t.contaOrigem === minhaConta ||
        (t.contaDestino === minhaConta && t.valor > 0)
      );
    }

    return t.contaOrigem === minhaConta;
  })
    .slice(0, 3)
    .map(t => {

      const isEntrada = this.isEntrada(t);

      let nomeDestinatario = '';

      if (t.tipo === 'TRANSFERENCIA') {
        if (isEntrada) {
          const origem = clientes.find(c => c.numberAccount === t.contaOrigem);
          nomeDestinatario = origem?.name || '';
        } else {
          const destino = clientes.find(c => c.numberAccount === t.contaDestino);
          nomeDestinatario = destino?.name || '';
        }
      }

      return {
        ...t,
        operacao: this.getOperacao(t),
        isEntrada,
        nomeDestinatario
      };
    });
}
}