import { Component, Inject, OnInit } from '@angular/core';
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
export class Operacoes implements OnInit {

  tabAtiva = 0;
  cliente!: Customer;

  saldoAtual = 0;
  limiteTotal = 0;
  limiteAtual = 0;

  contaLogada = 0;  // Mudado para number (contaId, não numberAccount)
  valor = 0;
  valorFormatado = '';
  contaDestino = "";
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

    this.nomeCliente = this.cliente.name;
    this.emailCliente = this.cliente.email;
  }

  ngOnInit() {
    // Buscar o ID da conta (não o número da conta)
    if (this.cliente.id) {
      this.customerService.obterClientesPendentes().subscribe({
        next: (clientes) => {
          const clienteData = clientes.find(c => c.id === this.cliente.id);
          if (clienteData) {
            // contaLogada já deve estar no cliente
            console.log('Cliente:', clienteData);
          }
        }
      });
    }
  }

  get valorValido(): boolean {
    if (this.tabAtiva === 0) {
      return this.valor > 0 && this.contaDestino.length > 0;
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

    // Se contaLogada ainda não foi carregada, usar o id do cliente
    const idConta = this.contaLogada > 0 ? this.contaLogada : this.cliente.id || 0;

    if (idConta <= 0) {
      this.erro = 'Erro: Conta não identificada. Tente fazer login novamente.';
      return;
    }

    switch (this.tabAtiva) {
      case 0: // transferência
        if (!this.contaDestino) {
          this.erro = 'Informe a conta destino.';
          return;
        }
        if (this.contaDestino === idConta.toString()) {
          this.erro = 'Não pode transferir para si mesmo.';
          return;
        }

        this.transactionService.transferir(idConta, this.contaDestino, this.valor).subscribe({
          next: () => {
            this.sucesso = 'Transferência realizada com sucesso!';
            this.resetCampos();
            setTimeout(() => this.dialogRef.close(), 2000);
          },
          error: (err) => {
            this.erro = 'Erro ao realizar transferência. Verifique o saldo ou a conta destino.';
            console.error(err);
          }
        });
        break;

      case 1: // saque
        this.transactionService.sacar(idConta, this.valor).subscribe({
          next: () => {
            this.sucesso = 'Saque realizado com sucesso!';
            this.resetCampos();
            setTimeout(() => this.dialogRef.close(), 2000);
          },
          error: (err) => {
            this.erro = 'Erro ao realizar saque.';
            console.error(err);
          }
        });
        break;

      case 2: // deposito
        this.transactionService.depositar(idConta, this.valor).subscribe({
          next: () => {
            this.sucesso = 'Depósito realizado com sucesso!';
            this.resetCampos();
            setTimeout(() => this.dialogRef.close(), 2000);
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
    this.contaDestino = "";
  }

  selecionarTab(index: number) {
    this.tabAtiva = index;
    this.resetForm();
  }

  resetForm() {
    this.valor = 0;
    this.valorFormatado = '';
    this.contaDestino = "";
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
