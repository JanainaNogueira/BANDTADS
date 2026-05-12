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
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule, FormsModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager implements OnInit {
  managers: ManagerSummary[] = [];
  searchTerm = '';
  selectedStatus: ManagerStatus | 'all' = 'all';

  totalSaldosPositivos = 0;
  totalSaldosNegativos = 0;

  constructor(
    private dialog: MatDialog,
    private managerService: ManagerService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregarGerentes();
    this.calcularSaldosDashboard();
  }

  private calcularSaldosDashboard(): void {
    const saldos = MOCK_CUSTOMERS.map((cliente) => cliente.balance || 0);
    this.totalSaldosPositivos = saldos.filter(s => s > 0).reduce((acc, s) => acc + s, 0);
    this.totalSaldosNegativos = saldos.filter(s => s < 0).reduce((acc, s) => acc + s, 0);
  }

  carregarGerentes(): void {
    this.managerService.listar().subscribe({
      next: (res) => { this.managers = res; },
      error: () => { this.showMessage('Erro ao carregar gerentes'); }
    });
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

  abrirModalEditar(gerente: ManagerSummary): void {
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

  abrirModalRemover(manager: ManagerSummary): void {
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

  get filteredManagers(): ManagerSummary[] {
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

  private getCountByStatus(status: ManagerStatus): number {
    return this.managers.filter((m) => m.status === status).length;
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', { duration: 3000 });
  }
}