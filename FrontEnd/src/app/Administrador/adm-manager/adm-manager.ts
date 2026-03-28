import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormManager } from './components/form-manager/form-manager';
import { Menu } from '../../components/menu/menu';
import { MatIconModule } from '@angular/material/icon';
import { ManagerStatus, MOCK_MANAGERS_LIST } from '../../../assets/mock/managers.mock';
import { ManagerCreateEditList } from '../../models/manager.model';

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager {
  constructor(private dialog: MatDialog) { }

  ngOnInit() {
    const dadosSalvos = localStorage.getItem('managers');

    if (dadosSalvos) {
      this.managers = JSON.parse(dadosSalvos);
    } else {
      this.managers = [...MOCK_MANAGERS_LIST];
    }
  }

  managers: ManagerCreateEditList[] = MOCK_MANAGERS_LIST;
  searchTerm = '';
  selectedStatus: ManagerStatus | 'all' = 'all';

  abrirModalCriar(): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: { modo: 'criar' }
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res && res.modo === 'criar') {
        const novoGerente: ManagerCreateEditList = {
          ...res.gerente,
          id: this.managers.length > 0 ? Math.max(...this.managers.map(m => m.id)) + 1 : 1,
          status: 'pending',
          clientes: 0
        };

        this.managers = [...this.managers, novoGerente];
        this.salvarNoStorage();
      }
    });
  }

  abrirModalEditar(gerente: ManagerCreateEditList): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: {
      modo: 'editar',
      gerente: { ...gerente }
    }
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res && res.modo === 'editar') {
        this.managers = this.managers.map((m) => {
          if (m.id === res.id) {
            return {
              ...m,
              ...res.gerente
            };
          }
          return m;
        });
        this.salvarNoStorage();
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

  private salvarNoStorage() {
    localStorage.setItem('managers', JSON.stringify(this.managers));
  }

  get filteredManagers(): ManagerCreateEditList[] {
    const normalizedTerm = this.searchTerm.trim().toLowerCase();

    return this.managers.filter((manager) => {
      const matchesStatus = this.selectedStatus === 'all' || manager.status === this.selectedStatus;
      const matchesSearch =
        !normalizedTerm ||
        manager.nome.toLowerCase().includes(normalizedTerm) ||
        manager.email.toLowerCase().includes(normalizedTerm);

      return matchesStatus && matchesSearch;
    });
  }

  selectStatus(status: ManagerStatus | 'all'): void {
    this.selectedStatus = status;
  }

  getInitials(nome: string): string {
    if (!nome) return '?';

    return nome
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
