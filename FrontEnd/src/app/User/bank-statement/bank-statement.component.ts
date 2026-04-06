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

export interface GrupoDia {
  data: Date;
  dataFormatada: string;
  transacoes: Transacao[];
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
  constructor(private transactionService: TransactionService) {}

  dataInicio = new FormControl<Date | null>(null, Validators.required);
  dataFim = new FormControl<Date | null>(null, Validators.required);

  gruposDia: GrupoDia[] = [];
  pesquisaRealizada = false;
  erroValidacao = '';

  pesquisar(): void {
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

    const mapaGrupos = new Map<string, Transacao[]>();

    const cursor = new Date(startDate);
    while (cursor <= endDate) {
      mapaGrupos.set(this.toDateKey(cursor), []);
      cursor.setDate(cursor.getDate() + 1);
    }

    for (const t of this.transactionService.getAllTransactions()) {
      const tDia = new Date(t.dataHora);
      tDia.setHours(0, 0, 0, 0);

      if (tDia >= startDate && tDia <= endDate) {
        mapaGrupos.get(this.toDateKey(tDia))?.push(t);
      }
    }

    this.gruposDia = Array.from(mapaGrupos.entries())
      .map(([key, transacoes]) => {
        const [y, m, d] = key.split('-').map(Number);
        const data = new Date(y, m - 1, d);

        // Ordena por transação mais recente baseado no horário
        transacoes.sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());

        return {
          data,
          dataFormatada: data.toLocaleDateString('pt-BR'),
          transacoes,
          saldoDia: transacoes.reduce((soma, t) => soma + t.valor, 0),
          aberto: false
        };
      })
      // Mais recente baseaado no dia
      .sort((a, b) => b.data.getTime() - a.data.getTime());

    this.pesquisaRealizada = true;
  }

  toggleDia(grupo: GrupoDia): void {
    if (grupo.transacoes.length > 0) {
      grupo.aberto = !grupo.aberto;
    }
  }

  formatarValor(valor: number): string {
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
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