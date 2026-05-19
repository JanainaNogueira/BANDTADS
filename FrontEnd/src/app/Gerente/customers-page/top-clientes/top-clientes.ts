import { Component, OnInit } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { Customer } from '../../../models/customer.model';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-top-clientes',
  standalone: true,
  imports: [MatIcon, CommonModule],
  templateUrl: './top-clientes.html',
  styleUrl: './top-clientes.css',
})
export class TopClientes implements OnInit {

  top3: Customer[] = [];
  carregando = true;
  mensagem = '';

  constructor(private readonly customerService: CustomerService) {}

  ngOnInit(): void {
    this.carregarTop3Clientes();
  }

  private carregarTop3Clientes(): void {
  this.customerService.obterTodosClientes().subscribe({
    next: (clientes) => {
      if (!clientes || clientes.length === 0) {
        this.mensagem = 'Nenhum cliente encontrado.';
        this.carregando = false;
        return;
      }

      this.top3 = clientes
        .sort((a, b) => (b.balance ?? 0) - (a.balance ?? 0))
        .slice(0, 3);

      this.carregando = false;
    },
    error: (err) => {
      console.error('Erro ao carregar top 3 clientes', err);
      this.mensagem = 'Erro ao carregar dados. Tente novamente.';
      this.carregando = false;
    }
  });
}

  formatarValor(valor: number): string {
    return (valor ?? 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  formatarCpf(cpf: string): string {
    const digits = (cpf || '').replace(/\D/g, '').slice(0, 11);
    if (digits.length !== 11) return cpf || '-';
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9, 11)}`;
  }
}