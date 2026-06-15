import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';

import { FormManager } from './components/form-manager/form-manager';
import { RemoveManager } from './components/remove-manager/remove-manager';
import { Menu } from '../../components/menu/menu';
import { ManagerSummary, ManagerStatus } from '../../models/manager.model';
import { ManagerService } from './services/manager.service';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';

interface ManagerDashboard extends ManagerSummary {
  clientCount: number;
  positiveSaldo: number;
  negativeSaldo: number;
}

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule, FormsModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager implements OnInit {
  managers: ManagerDashboard[] = [];
  searchTerm = '';
  selectedStatus: ManagerStatus | 'all' = 'all';
  carregando = true;

  totalSaldosPositivos = 0;
  totalSaldosNegativos = 0;

  constructor(
    private dialog: MatDialog,
    private managerService: ManagerService,
    private customerService: CustomerService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregarDashboard();
  }

  private carregarDashboard(): void {
    this.carregando = true;
    
    this.managerService.listar().subscribe({
      next: (gerentes) => {
        this.customerService.obterRelatorioClientes().subscribe({
          next: (clientes) => {
            this.construirDashboard(gerentes, clientes);
            this.carregando = false;
          },
          error: () => {
            this.showMessage('Erro ao carregar clientes');
            this.managers = gerentes.map(g => ({
              ...g,
              clientCount: 0,
              positiveSaldo: 0,
              negativeSaldo: 0
            }));
            this.carregando = false;
          }
        });
      },
      error: () => {
        this.showMessage('Erro ao carregar gerentes');
        this.carregando = false;
      }
    });
  }

  private construirDashboard(gerentes: ManagerSummary[], clientes: Customer[]): void {
    this.managers = gerentes.map(gerente => {
      const clientesDoGerente = clientes.filter(c => c.manager?.cpf === gerente.cpf);
      
      const positiveSaldo = clientesDoGerente
        .filter(c => (c.balance ?? 0) >= 0)
        .reduce((acc, c) => acc + (c.balance ?? 0), 0);
      
      const negativeSaldo = clientesDoGerente
        .filter(c => (c.balance ?? 0) < 0)
        .reduce((acc, c) => acc + (c.balance ?? 0), 0);

      return {
        ...gerente,
        clientCount: clientesDoGerente.length,
        positiveSaldo: positiveSaldo,
        negativeSaldo: negativeSaldo
      };
    }).sort((a, b) => b.positiveSaldo - a.positiveSaldo);

    this.totalSaldosPositivos = this.managers.reduce((acc, m) => acc + m.positiveSaldo, 0);
    this.totalSaldosNegativos = this.managers.reduce((acc, m) => acc + m.negativeSaldo, 0);
  }

  carregarGerentes(): void {
    this.carregarDashboard();
  }

  abrirModalCriar(): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: { modo: 'criar' }
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res?.modo === 'criar') {
        this.managerService.criar(res.gerente).subscribe({
          next: () => {
            this.showMessage('Gerente criado com sucesso!');
            this.carregarGerentes();
          },
          error: () => this.showMessage('Erro ao criar gerente!')
        });
      }
    });
  }

  abrirModalEditar(gerente: ManagerDashboard): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: { modo: 'editar', gerente: gerente }
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res?.modo === 'editar') {
        this.managerService.atualizar(res.gerente.id, res.gerente).subscribe({
          next: () => {
            this.showMessage('Gerente editado com sucesso!');
            this.carregarGerentes();
          },
          error: () => this.showMessage('Erro ao editar gerente.')
        });
      }
    });
  }

  abrirModalRemover(manager: ManagerDashboard): void {
    const dialogRef = this.dialog.open(RemoveManager, {
      width: '560px',
      data: { managerCpf: manager.cpf },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.success) {
        this.managerService.deletar(manager.id).subscribe({
          next: () => {
            this.showMessage('Gerente removido.');
            this.carregarGerentes();
          },
          error: () => this.showMessage('Erro ao remover gerente.')
        });
      }
    });
  }

  get statusTabs(): Array<{ key: ManagerStatus | 'all'; label: string; count: number }> {
    return [
      { key: 'all', label: 'Todos', count: this.managers.length },
      { key: 'active', label: 'Ativos', count: this.getCountByStatus('active') },
      { key: 'inactive', label: 'Inativos', count: this.getCountByStatus('inactive') }
    ];
  }

  get filteredManagers(): ManagerDashboard[] {
    const term = this.searchTerm.trim().toLowerCase();
    return this.managers.filter((m) => {
      const matchesStatus = this.selectedStatus === 'all' || m.status === this.selectedStatus;
      const matchesSearch = !term || m.name.toLowerCase().includes(term) || m.email.toLowerCase().includes(term);
      return matchesStatus && matchesSearch;
    });
  }

  selectStatus(status: ManagerStatus | 'all'): void {
    this.selectedStatus = status;
  }

  getInitials(name: string): string {
    if (!name) return '?';
    return name.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase();
  }

  getStatusLabel(status: ManagerStatus): string {
    return status === 'active' ? 'Ativo' : 'Inativo';
  }

  formatarValor(valor: number): string {
    return (valor ?? 0).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }

  private getCountByStatus(status: ManagerStatus): number {
    return this.managers.filter((m) => m.status === status).length;
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', { duration: 3000 });
  }
}