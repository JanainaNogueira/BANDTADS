import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Customer } from '../../../../models/customer.model';
import { TransactionService } from '../../../../services/transaction.service';
import { CustomerService } from '../../../../services/customer.service';


@Component({
  selector: 'app-operacoes',
  imports: [CommonModule, FormsModule, MatIconModule, MatDialogModule],
  templateUrl: './operacoes.html',
  styleUrl: './operacoes.css',
})
export class Operacoes {

  tabAtiva = 0;
  cliente!: Customer;

  saldoAtual = 0;
  limiteTotal = 0;
  limiteAtual = 0;

  contaLogada = 0;
  valor = 0;
  valorFormatado = '';
  contaDestino = 0;
  erro = '';
  sucesso = '';

  nomeCliente = '';
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
      cliente: Customer;
    }
  ) {
    this.cliente = data.cliente;
    this.tabAtiva = data?.tabInicial ?? 0;

    this.saldoAtual = this.cliente.balance;
    this.limiteTotal = this.cliente.limit;
    this.limiteAtual = this.cliente.limit;

    this.contaLogada = this.cliente.numberAccount;
    this.nomeCliente = this.cliente.name;
    this.emailCliente = this.cliente.email;
  }

  get valorValido(): boolean {
    if (this.tabAtiva === 0) {
      return this.valor > 0 && this.contaDestino > 0;
    }
    return this.valor > 0;
  }

  get saldoDisponivelTotal(): number {
    return this.saldoAtual + this.limiteAtual;
  }

  onValorInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let digits = input.value.replace(/\D/g, '');

    if (!digits) {
      this.valor = 0;
      this.valorFormatado = '';
      return;
    }

    const numericValue = parseInt(digits, 10) / 100;
    this.valor = numericValue;

    this.valorFormatado = 'R$ ' + numericValue.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
    });

    this.erro = '';
    this.sucesso = '';
  }

  executar() {
    this.erro = '';
    this.sucesso = '';

    if (this.valor <= 0) {
      this.erro = 'Informe um valor válido.';
      return;
    }

    if (this.tabAtiva !== 2 && this.valor > this.saldoDisponivelTotal) {
      this.erro = 'Saldo + limite insuficiente.';
      return;
    }

    switch (this.tabAtiva) {
      case 0: // transferência
        if (!this.contaDestino) {
          this.erro = 'Informe a conta destino.';
          return;
        }
        if (this.contaDestino === this.contaLogada) {
          this.erro = 'Não pode transferir para si mesmo.';
          return;
        }

        this.transactionService.transferir(this.contaLogada, this.contaDestino.toString(), this.valor).subscribe({
          next: () => {
            this.sucesso = 'Transferência realizada com sucesso!';
            this.resetCampos();
          },
          error: (err) => {
            this.erro = 'Erro ao realizar transferência. Verifique o saldo ou a conta destino.';
            console.error(err);
          }
        });
        break;

      case 1: // saque
        this.transactionService.sacar(this.contaLogada, this.valor).subscribe({
          next: () => {
            this.sucesso = 'Saque realizado com sucesso!';
            this.resetCampos();
          },
          error: (err) => {
            this.erro = 'Erro ao realizar saque.';
            console.error(err);
          }
        });
        break;

      case 2: // deposito
        this.transactionService.depositar(this.contaLogada, this.valor).subscribe({
          next: () => {
            this.sucesso = 'Depósito realizado com sucesso!';
            this.resetCampos();
          },
          error: (err) => {
            this.erro = 'Erro ao realizar depósito.';
            console.error(err);
          }
        });
        break;
    }
  }

  private resetCampos() {
    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = 0;
  }

  selecionarTab(index: number) {
    this.tabAtiva = index;
    this.resetForm();
  }

  resetForm() {
    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = 0;
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
      case 2: return 'Valor do Depósito';
      default: return 'Valor';
    }
  }
}
