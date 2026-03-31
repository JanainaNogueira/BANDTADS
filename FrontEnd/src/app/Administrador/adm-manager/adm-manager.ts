import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormManager } from './components/form-manager/form-manager';
import { Menu } from '../../components/menu/menu';
import { ManagerCreateEdit, ManagerSummary } from '../../models/manager.model';
import {  MatIconModule } from '@angular/material/icon';
import { ManagerStatus, MOCK_MANAGERS_LIST, MOCK_MANAGERS_CREATE } from '../../../assets/mock/managers.mock';

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager {
  constructor(private dialog: MatDialog) {}

  mockGerente: ManagerCreateEdit = MOCK_MANAGERS_CREATE;
  managers: ManagerSummary[] = MOCK_MANAGERS_LIST;
  searchTerm = '';
  selectedStatus: ManagerStatus | 'all' = 'all';


  abrirModalCriar(): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: { modo: 'criar' }
    });

    dialogRef.afterClosed().subscribe((dados) => {
      if (dados) {
        console.log('Criar gerente:', dados);
      }
    });
  }

  abrirModalEditar(gerente: ManagerCreateEdit): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: {
        modo: 'editar',
        gerente: this.mockGerente
      }
    });

    dialogRef.afterClosed().subscribe((dados) => {
      if (dados) {
        console.log('Editar gerente:', dados);
      }
    });
  }

  get statusTabs(): Array<{ key: ManagerStatus | 'all'; label: string; count: number }> {
    return [
      { key: 'all', label: 'Todos', count: this.managers.length },
      { key: 'active', label: 'Ativos', count: this.getCountByStatus('active') },
      { key: 'pending', label: 'Pendentes', count: this.getCountByStatus('pending') },
      { key: 'inactive', label: 'Inativos', count: this.getCountByStatus('inactive') }
    ];
  }

  get filteredManagers(): ManagerSummary[] {
    const normalizedTerm = this.searchTerm.trim().toLowerCase();

    return this.managers.filter((manager) => {
      const matchesStatus = this.selectedStatus === 'all' || manager.status === this.selectedStatus;
      const matchesSearch =
        !normalizedTerm ||
        manager.name.toLowerCase().includes(normalizedTerm) ||
        manager.email.toLowerCase().includes(normalizedTerm);

      return matchesStatus && matchesSearch;
    });
  }

  selectStatus(status: ManagerStatus | 'all'): void {
    this.selectedStatus = status;
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map((part) => part[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  getStatusLabel(status: ManagerStatus): string {
    if (status === 'active') {
      return 'Ativo';
    }

    if (status === 'pending') {
      return 'Pendente';
    }

    return 'Inativo';
  }

  approveManager(managerId: number): void {
    this.updateManagerStatus(managerId, 'active');
  }

  rejectManager(managerId: number): void {
    this.updateManagerStatus(managerId, 'inactive');
  }

  private getCountByStatus(status: ManagerStatus): number {
    return this.managers.filter((manager) => manager.status === status).length;
  }

  private updateManagerStatus(managerId: number, status: ManagerStatus): void {
    this.managers = this.managers.map((manager) => {
      if (manager.id !== managerId) {
        return manager;
      }

      return {
        ...manager,
        status
      };
    });
  }
}
