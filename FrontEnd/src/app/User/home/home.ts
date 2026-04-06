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

  transactions: Transacao[] = [];

  email: string = '';
  tipo: string = "";
  login:Customer | null = null;

  ngOnInit() {
    this.carregarUsuarioLogado();
    this.carregarTransacoesRecentes();
  }

  private carregarUsuarioLogado(): void {
    if (typeof localStorage === 'undefined') {
      return;
    }

    this.email = localStorage.getItem('email') || '';
    this.tipo = localStorage.getItem('tipoUsuario') || "";
    if (this.tipo == "cliente") {
      this.login = this.customerService
        .obterTodosClientes()
        .find((cliente) => cliente.email == this.email) || null;
    }
  }

  private carregarTransacoesRecentes(): void {
    this.transactions = this.transactionService.getRecentTransactions(3);
  }

  abrirOperacoes(tabInicial: number): void {
    const ref = this.dialog.open(Operacoes, {
      data: {
        tabInicial,
        saldoAtual: this.login?.balance,
        limiteTotal: this.login?.limit,
        contaLogada: String(this.login?.numberAccount || ''),
        nomeCliente: this.login?.name,
        emailCliente: this.login?.email,
      },
      width: '760px',
      maxWidth: '96vw'
    });

    ref.afterClosed().subscribe(() => {
      this.carregarUsuarioLogado();
      this.carregarTransacoesRecentes();
    });
  };

  irParaExtrato(): void {
    this.router.navigate(['/bank-statement']);
  }
}
