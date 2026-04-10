import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Customer } from '../../../../models/costumer.model';
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

  descontarValor(valor: number) {
    if (valor <= this.saldoAtual) {
      this.saldoAtual -= valor;
    } else {
      const restante = valor - this.saldoAtual;
      this.saldoAtual = 0;
      this.limiteAtual -= restante;
    }
  }

  depositarValor(valor: number) {
    if (valor <= 0) {
      this.erro = 'Valor inválido.';
      return;
    }

    this.saldoAtual += valor;
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

    let destino: Customer | undefined;

    switch (this.tabAtiva) {
      //tranferencia
      case 0:

        if (!this.contaDestino) {
          this.erro = 'Informe a conta destino.';
          return;
        }

        if (this.contaDestino === this.contaLogada) {
          this.erro = 'Não pode transferir para si mesmo.';
          return;
        }

        destino = this.customerService
          .obterTodosClientes()
          .find(c => c.numberAccount === Number(this.contaDestino));

        if (!destino) {
          this.erro = 'Conta destino não encontrada.';
          return;
        }

        // saldo
        this.descontarValor(this.valor);
        destino.balance += this.valor;

        this.customerService.atualizarCliente(destino);

        this.transactionService.transferir(
          this.contaLogada,
          destino.numberAccount,
          this.valor
        );

        this.sucesso = 'Transferência realizada com sucesso!';
        break;

      // saque
      case 1:

        if (this.valor > this.saldoAtual) {
          this.erro = 'Saldo insuficiente para saque.';
          return;
        }

        this.saldoAtual -= this.valor;

        this.transactionService.addTransaction({
          tipo: 'SAQUE',
          valor: -this.valor,
          contaOrigem: this.contaLogada
        });

        this.sucesso = 'Saque realizado com sucesso!';
  break;

      // deposito
      case 2:

        this.depositarValor(this.valor);

        this.transactionService.addTransaction({
          tipo: 'DEPOSITO',
          valor: this.valor,
          contaOrigem: this.contaLogada
        });

        this.sucesso = 'Depósito realizado com sucesso!';
        break;
    }

    this.persistirSaldoCliente();

    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = 0;
  }

  private persistirSaldoCliente(): void {
    const clientes = this.customerService.obterTodosClientes();

    const index = clientes.findIndex(
      c => c.email.toLowerCase() === this.emailCliente.toLowerCase()
    );

    if (index === -1) return;

    clientes[index] = {
      ...clientes[index],
      balance: this.saldoAtual,
      limit: this.limiteAtual
    };

    this.customerService.salvarClientes(clientes);
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