import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Menu } from '../../components/menu/menu';
import { TabsOperacao } from '../../components/tabs-operacao/tabs-operacao';

@Component({
  selector: 'app-depositar',
  imports: [CommonModule, FormsModule, MatIconModule, Menu, TabsOperacao],
  templateUrl: './depositar.html',
  styleUrl: './depositar.css',
})
export class Depositar {
  saldoAtual = 200.00;
  valorDeposito = 0;
  valorFormatado = '';
  erro = '';
  sucesso = '';

  get valorValido(): boolean {
    return this.valorDeposito > 0;
  }

  onValorInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let digits = input.value.replace(/\D/g, '');

    if (digits.length === 0) {
      this.valorDeposito = 0;
      this.valorFormatado = '';
      return;
    }

    const numericValue = parseInt(digits, 10) / 100;
    this.valorDeposito = numericValue;
    this.valorFormatado = 'R$ ' + numericValue.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    this.erro = '';
    this.sucesso = '';
  }

  depositar() {
    this.erro = '';
    this.sucesso = '';

    if (this.valorDeposito <= 0) {
      this.erro = 'Informe um valor maior que zero.';
      return;
    }

    this.saldoAtual += this.valorDeposito;
    this.sucesso = `Depósito de R$ ${this.valorDeposito.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} realizado com sucesso!`;
    this.valorDeposito = 0;
    this.valorFormatado = '';
  }
}
