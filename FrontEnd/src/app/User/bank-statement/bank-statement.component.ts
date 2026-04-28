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
  this.transactionService.consultarExtrato(this.login.numberAccount, startDate, endDate).subscribe({
    next: (extrato: Transacao[]) => {
      const clientes = this.customerService.obterTodosClientes();

      // Mapear transações para os grupos
      for (const t of extrato) {
        t.dataHora = (t as any).dataHora ? new Date((t as any).dataHora) : new Date();
        const tDia = new Date(t.dataHora);
        tDia.setHours(0, 0, 0, 0);

        const key = this.toDateKey(tDia);
        const grupo = mapaGrupos.get(key);
        if (!grupo) continue;

        // Regra de Cores (R8): Saída = Vermelho, Entrada = Azul
        // Em transferências, depende se o cliente logado é origem ou destino
        // O valor vindo do back-end já deve vir negativo para saídas e positivo para entradas na conta consultada
        const isEntrada = t.valor >= 0;

        let nomeOutro = '';
        if (t.tipo === 'TRANSFERENCIA') {
           // No back-end, salvamos clienteOrigemId e clienteDestinoId
           const origId = (t as any).clienteOrigemId;
           const destId = (t as any).clienteDestinoId;

           if (isEntrada) {
             // Se é entrada, o outro é quem enviou (origem)
             const origem = clientes.find(c => c.id === origId || c.numberAccount === origId);
             nomeOutro = origem ? `De: ${origem.name || origem.nome}` : 'Origem Desconhecida';
           } else {
             // Se é saída, o outro é quem recebeu (destino)
             const destino = clientes.find(c => c.id === destId || c.numberAccount === destId);
             nomeOutro = destino ? `Para: ${destino.name || destino.nome}` : 'Destino Desconhecido';
           }
        } else {
          nomeOutro = isEntrada ? 'Depósito' : 'Saque';
        }

        grupo.push({ ...t, isEntrada, nomeOutro });
      }

      // Cálculo de Saldo Consolidado (R8)
      // Precisamos do saldo atual e ir voltando no tempo para calcular o saldo de cada dia
      let saldoCorrente = this.login!.balance;
      
      const chavesOrdenadas = Array.from(mapaGrupos.keys()).sort().reverse(); // Do dia mais recente para o mais antigo
      
      this.gruposDia = chavesOrdenadas.map(key => {
          const transacoes = mapaGrupos.get(key) || [];
          const [y, m, d] = key.split('-').map(Number);
          const data = new Date(y, m - 1, d);

          // Ordenar transações do dia (mais recentes primeiro)
          transacoes.sort((a, b) => {
            const timeA = a.dataHora ? a.dataHora.getTime() : 0;
            const timeB = b.dataHora ? b.dataHora.getTime() : 0;
            return timeB - timeA;
          });

          // O saldo consolidado do dia é o saldo ao FINAL do dia.
          const saldoAoFinalDoDia = saldoCorrente;
          
          // Para o próximo dia (que é anterior no tempo), subtraímos o que entrou e somamos o que saiu hoje
          const totalMovimentadoHoje = transacoes.reduce((soma, t) => soma + t.valor, 0);
          saldoCorrente -= totalMovimentadoHoje;

          return {
            data,
            dataFormatada: data.toLocaleDateString('pt-BR'),
            transacoes,
            saldoDia: saldoAoFinalDoDia,
            aberto: transacoes.length > 0 // Deixar aberto se houver transações
          };
      });

      this.pesquisaRealizada = true;
    },
    error: (err) => {
      this.erroValidacao = 'Erro ao consultar o extrato.';
      console.error(err);
    }
  });

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