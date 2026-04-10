import { Component } from '@angular/core'; 
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { Menu } from '../../components/menu/menu';
import { Transacao, TransactionService } from '../../services/transaction.service';
import { CustomerService} from '../../services/customer.service';
import { Customer } from '../../models/costumer.model';

export interface TransacaoExtrato extends Transacao {
  isEntrada: boolean;
  nomeOutro: string;
}

export interface GrupoDia {
  data: Date;
  dataFormatada: string;
  transacoes: TransacaoExtrato[];
  saldoDia: number;
  aberto: boolean;
}

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [
    Menu,
    CommonModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatNativeDateModule,
    DatePipe,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
  ],
  templateUrl: './bank-statement.component.html',
  styleUrl: './bank-statement.component.css',
})
export class BankStatementComponent {
  
  constructor(
    private transactionService: TransactionService,
    private customerService: CustomerService
  ) {}

  dataInicio = new FormControl<Date | null>(null, Validators.required);
  dataFim = new FormControl<Date | null>(null, Validators.required);

  gruposDia: GrupoDia[] = [];
  pesquisaRealizada = false;
  erroValidacao = '';

  login: Customer | null = null;

  ngOnInit() {
    this.carregarUsuarioLogado();
  }

  private carregarUsuarioLogado() {
    const email = localStorage.getItem('email') || '';
    this.login = this.customerService
      .obterTodosClientes()
      .find(c => c.email === email) || null;
  }

  pesquisar(): void {
  if (!this.login) {
    this.erroValidacao = 'Cliente não carregado';
    return;
  }

  const inicio = this.dataInicio.value;
  const fim = this.dataFim.value;
  this.erroValidacao = '';

  if (!inicio || !fim) {
    this.erroValidacao = 'Selecione ambas as datas';
    return;
  }
  if (fim < inicio) {
    this.erroValidacao = 'Data de fim não pode ser anterior à data de início';
    return;
  }

  const startDate = new Date(inicio);
  startDate.setHours(0, 0, 0, 0);
  const endDate = new Date(fim);
  endDate.setHours(23, 59, 59, 999);

  const mapaGrupos = new Map<string, TransacaoExtrato[]>();

  const cursor = new Date(startDate);
  while (cursor <= endDate) {
    mapaGrupos.set(this.toDateKey(cursor), []);
    cursor.setDate(cursor.getDate() + 1);
  }

  // Transações
  const todas = this.transactionService.getTransactionsByAccount(this.login.numberAccount);
  const clientes = this.customerService.obterTodosClientes();

  for (const t of todas) {
    if (t.contaOrigem !== this.login.numberAccount && t.contaDestino !== this.login.numberAccount) {
      continue;
    }

    const tDia = new Date(t.dataHora);
    tDia.setHours(0, 0, 0, 0);

    const grupo = mapaGrupos.get(this.toDateKey(tDia));
    if (!grupo) continue;

    const isEntrada = this.transactionService.isEntrada(t, this.login.numberAccount);

    let nomeOutro = '';
    if (t.tipo === 'TRANSFERENCIA') {
      if (isEntrada) {
        const origem = clientes.find(c => c.numberAccount === t.contaOrigem);
        nomeOutro = origem?.name || '';
      } else {
        const destino = clientes.find(c => c.numberAccount === t.contaDestino);
        nomeOutro = destino?.name || '';
      }
    } else {
      nomeOutro = isEntrada ? 'Depósito' : 'Você';
    }

    grupo.push({ ...t, isEntrada, nomeOutro });
  }

  this.gruposDia = Array.from(mapaGrupos.entries())
    .map(([key, transacoes]) => {
      const [y, m, d] = key.split('-').map(Number);
      const data = new Date(y, m - 1, d);

      transacoes.sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());

      const saldoDia = transacoes.reduce((soma, t) => soma + t.valor, 0);

      return {
        data,
        dataFormatada: data.toLocaleDateString('pt-BR'),
        transacoes,
        saldoDia,
        aberto: false
      };
    })
    .sort((a, b) => b.data.getTime() - a.data.getTime());

  this.pesquisaRealizada = true;
}

  toggleDia(grupo: GrupoDia): void {
    if (grupo.transacoes.length > 0) {
      grupo.aberto = !grupo.aberto;
    }
  }

  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  trackByData(index: number, grupo: GrupoDia): number {
    return grupo.data.getTime();
  }

  private toDateKey(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
}