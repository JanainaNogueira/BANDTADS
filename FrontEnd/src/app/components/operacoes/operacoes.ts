import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CustomerService } from '../../services/customer.service';
import { TipoOperacao, TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-operacoes',
  imports: [CommonModule, FormsModule, MatIconModule, MatDialogModule],
  templateUrl: './operacoes.html',
  styleUrl: './operacoes.css',
})
export class Operacoes {
  tabAtiva = 0;
  saldoAtual = 200.00;
  limiteTotal = 1000.00;
  contaLogada = '1234';
  valor = 0;
  valorFormatado = '';
  contaDestino = '';
  erro = '';
  sucesso = '';
  dataHoraUltimaTransferencia: Date | null = null;
  nomeCliente = 'Cliente';
  emailCliente = '';

  tabs = [
    { label: 'Transferência', icon: 'person_outline' },
    { label: 'Saque', icon: 'shield_outlined' },
    { label: 'Depositar', icon: 'notifications_none' },
  ];

  constructor(
    public dialogRef: MatDialogRef<Operacoes>,
    private customerService: CustomerService,
    private transactionService: TransactionService,
    @Inject(MAT_DIALOG_DATA) public data: {
      tabInicial: number;
      saldoAtual?: number;
      limiteTotal?: number;
      contaLogada?: string;
      nomeCliente?: string;
      emailCliente?: string;
    }
  ) {
    this.tabAtiva = data?.tabInicial ?? 0;
    this.saldoAtual = data?.saldoAtual ?? this.saldoAtual;
    this.limiteTotal = data?.limiteTotal ?? this.limiteTotal;
    this.contaLogada = data?.contaLogada ?? this.contaLogada;
    this.nomeCliente = data?.nomeCliente ?? this.nomeCliente;
    this.emailCliente = data?.emailCliente ?? this.emailCliente;
  }

  get valorValido(): boolean {
    if (this.tabAtiva === 0) {
      return this.valor > 0 && this.contaDestino.trim().length > 0;
    }
    return this.valor > 0;
  }

  selecionarTab(index: number) {
    this.tabAtiva = index;
    this.resetForm();
  }

  onValorInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let digits = input.value.replace(/\D/g, '');

    if (digits.length === 0) {
      this.valor = 0;
      this.valorFormatado = '';
      return;
    }

    const numericValue = parseInt(digits, 10) / 100;
    this.valor = numericValue;
    this.valorFormatado = 'R$ ' + numericValue.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
    this.erro = '';
    this.sucesso = '';
  }

  executar() {
    this.erro = '';
    this.sucesso = '';

    if (this.valor <= 0) {
      this.erro = 'Informe um valor maior que zero.';
      return;
    }

    const valorOperacao = this.valor;

    switch (this.tabAtiva) {
      case 0:
        if (!this.contaDestino.trim()) {
          this.erro = 'Informe a conta de destino.';
          return;
        }
        if (this.contaDestino.trim() === this.contaLogada) {
          this.erro = 'A conta de destino deve ser diferente da sua conta.';
          return;
        }
        if (this.valor > this.saldoDisponivelComLimite) {
          this.erro = 'Saldo insuficiente (considerando limite disponível).';
          return;
        }
        this.saldoAtual -= this.valor;
        this.dataHoraUltimaTransferencia = new Date();
        this.sucesso = `Transferência de R$ ${this.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} realizada em ${this.dataHoraUltimaTransferencia.toLocaleString('pt-BR')}!`;
        break;
      case 1:
        if (this.valor > this.saldoDisponivelComLimite) {
          this.erro = 'Saldo insuficiente (considerando limite disponível).';
          return;
        }
        this.saldoAtual -= this.valor;
        this.sucesso = `Saque de R$ ${this.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} realizado com sucesso!`;
        break;
      case 2:
        this.saldoAtual += this.valor;
        this.sucesso = `Depósito de R$ ${this.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} realizado com sucesso!`;
        break;
    }

    this.persistirOperacao(valorOperacao);
    this.persistirSaldoCliente();

    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = '';
  }

  private persistirOperacao(valor: number): void {
    const mapaOperacao: Record<number, TipoOperacao> = {
      0: 'Transferência',
      1: 'Saque',
      2: 'Depósito',
    };

    const operacao = mapaOperacao[this.tabAtiva] ?? 'Transferência';
    const valorFinal = this.tabAtiva === 2 ? valor : -valor;

    this.transactionService.addTransaction({
      operacao,
      nomeCliente: this.nomeCliente,
      valor: valorFinal,
      isEntrada: valorFinal > 0,
    });
  }

  private persistirSaldoCliente(): void {
    if (!this.emailCliente) {
      return;
    }

    const clientes = this.customerService.obterTodosClientes();
    const index = clientes.findIndex(
      (cliente) => cliente.email.toLowerCase() === this.emailCliente.toLowerCase()
    );

    if (index === -1) {
      return;
    }

    clientes[index] = {
      ...clientes[index],
      balance: this.saldoAtual,
    };

    this.customerService.salvarClientes(clientes);
  }

  resetForm() {
    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = '';
    this.erro = '';
    this.sucesso = '';
  }

  fechar() {
    this.dialogRef.close();
  }

  get labelBotao(): string {
    return this.tabs[this.tabAtiva].label;
  }

  get labelValor(): string {
    switch (this.tabAtiva) {
      case 0: return 'Valor da Transferência';
      case 1: return 'Valor do Saque';
      case 2: return 'Valor do Deposito';
      default: return 'Valor';
    }
  }

  get saldoDisponivelComLimite(): number {
    return this.saldoAtual + this.limiteTotal;
  }
}
