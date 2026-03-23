import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MOCK_MANAGERS, ManagerSummary, ManagerStatus } from '../../../assets/mock/managers.mock';
import { Menu } from '../../components/menu/menu';

@Component({
  selector: 'app-managers-list',
  standalone: true,
  imports: [CommonModule, Menu],
  templateUrl: './managers-list.html',
  styleUrl: './managers-list.css',
})
export class ManagersList {
  managers: ManagerSummary[] = MOCK_MANAGERS;
  searchTerm = '';
  selectedStatus: ManagerStatus | 'all' = 'all';

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
