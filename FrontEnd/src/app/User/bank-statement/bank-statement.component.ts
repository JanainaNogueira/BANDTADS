import { Component } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { Menu } from '../../components/menu/menu';

export type TipoOperacao = 'Depósito' | 'Saque' | 'Transferência';

export interface Transacao {
  id: number;
  dataHora: Date;
  operacao: TipoOperacao;
  clienteOrigemDestino: string;
  valor: number;
  isEntrada: boolean;
}

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

  dataInicio = new FormControl<Date | null>(null, Validators.required);
  dataFim = new FormControl<Date | null>(null, Validators.required);

  gruposDia: GrupoDia[] = [];
  pesquisaRealizada = false;
  erroValidacao = '';

  private readonly todasTransacoes: Transacao[] = [
    { id: 1, dataHora: new Date(2026, 2, 5, 9, 15),  operacao: 'Depósito',      clienteOrigemDestino: 'Ana Silva',       valor: 12500.75, isEntrada: true  },
    { id: 2, dataHora: new Date(2026, 2, 5, 10, 30), operacao: 'Transferência', clienteOrigemDestino: 'Helena Rocha',    valor: 4500.00,  isEntrada: true  },
    { id: 3, dataHora: new Date(2026, 2, 5, 11, 0),  operacao: 'Transferência', clienteOrigemDestino: 'Juliana Ferreira',valor: 24000.00, isEntrada: true  },
    { id: 4, dataHora: new Date(2026, 2, 5, 13, 45), operacao: 'Saque',         clienteOrigemDestino: 'Fernanda Lima',   valor: -5000.00, isEntrada: false },
    { id: 5, dataHora: new Date(2026, 2, 5, 14, 20), operacao: 'Depósito',      clienteOrigemDestino: 'Carlos Mendes',   valor: 1200.00,  isEntrada: true  },
    { id: 6, dataHora: new Date(2026, 2, 5, 15, 0),  operacao: 'Transferência', clienteOrigemDestino: 'Carlos Mendes',   valor: -1200.00, isEntrada: false },
    { id: 7, dataHora: new Date(2026, 2, 5, 16, 30), operacao: 'Transferência', clienteOrigemDestino: 'Helena Rocha',    valor: 2340.20,  isEntrada: true  },
    { id: 8, dataHora: new Date(2026, 2, 6, 8, 0),   operacao: 'Depósito',      clienteOrigemDestino: 'Roberto Alves',   valor: 8000.00,  isEntrada: true  },
    { id: 9, dataHora: new Date(2026, 2, 6, 14, 0),  operacao: 'Saque',         clienteOrigemDestino: 'Roberto Alves',   valor: -3500.00, isEntrada: false },
    { id: 10, dataHora: new Date(2026, 2, 8, 10, 0), operacao: 'Transferência', clienteOrigemDestino: 'Maria Oliveira',  valor: 6750.00,  isEntrada: true  },
  ];

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

    for (const t of this.todasTransacoes) {
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