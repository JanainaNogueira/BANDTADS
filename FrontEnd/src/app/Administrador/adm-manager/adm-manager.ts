import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormManager } from './components/form-manager/form-manager';
import { Menu } from '../../components/menu/menu';
import { ManagerCreateEdit, ManagerSummary } from '../../models/manager.model';
import {  MatIconModule } from '@angular/material/icon';
import { ManagerStatus, MOCK_MANAGERS_LIST, MOCK_MANAGERS_CREATE } from '../../../assets/mock/managers.mock';
import { RemoveManager } from './components/remove-manager/remove-manager';
import { CustomerService } from '../../services/customer.service';

interface StoredManager {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager implements OnInit {
  private readonly MANAGERS_STORAGE_KEY = 'bantads_managers';

  constructor(
    private dialog: MatDialog,
    private customerService: CustomerService,
  ) {
  }

  ngOnInit(): void {
    const dadosSalvos = localStorage.getItem('managers');
    if (dadosSalvos) {
      this.managers = JSON.parse(dadosSalvos);
    } else {
      this.managers = [...MOCK_MANAGERS_LIST];
    }
    this.refreshManagerClientsCount();
    this.syncManagersStorage();
  }

  mockGerente: ManagerCreateEdit = MOCK_MANAGERS_CREATE;
  managers: ManagerSummary[] = [];
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
        const dados = res.gerente; 
        const novoGerente: ManagerSummary = {
          id: this.managers.length + 1,
          name: dados.nome,
          email: dados.email,
          phone: dados.telefone,
          status: 'pending',
          clients: 0
        };
        this.managers = [...this.managers, novoGerente];
        localStorage.setItem('managers', JSON.stringify(this.managers));
      }
    });
  }

  abrirModalEditar(gerente: ManagerSummary): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: {
        modo: 'editar',
        gerente: gerente
      }
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res && res.modo === 'editar') {
        const dados = res.gerente;
        this.managers = this.managers.map((m) => {
          if (m.id === dados.id) {
            return {
              ...m,
              name: dados.nome,
              email: dados.email,
              phone: dados.telefone
            };
          }
          return m;
        });
        localStorage.setItem('managers', JSON.stringify(this.managers));
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
    if (!name) return '?';

    return name
      .split(' ')
      .map((part) => part[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  getStatusLabel(status: ManagerStatus): string {
    if (status === 'active') return 'Ativo';
    if (status === 'pending') return 'Pendente';
    return 'Inativo';
  }

  approveManager(managerId: number): void {
    this.updateManagerStatus(managerId, 'active');
  }

  rejectManager(managerId: number): void {
    this.updateManagerStatus(managerId, 'inactive');
  }

  abrirModalRemover(manager: ManagerSummary): void {
    this.resolveManagerCpf(manager, (managerCpf) => {
      if (!managerCpf) return;

      const dialogRef = this.dialog.open(RemoveManager, {
        width: '560px',
        maxWidth: '95vw',
        data: { managerCpf },
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (!result?.success) return;
        this.managers = this.managers.filter((item) => item.id !== manager.id);
        this.refreshManagerClientsCount();
        this.syncManagersStorage();
      });
    });
  }

  private getCountByStatus(status: ManagerStatus): number {
    return this.managers.filter((manager) => manager.status === status).length;
  }

  private updateManagerStatus(managerId: number, status: ManagerStatus): void {
    this.managers = this.managers.map((manager) => {
      if (manager.id !== managerId) return manager;
      return { ...manager, status };
    });
  }

  private refreshManagerClientsCount(): void {
    this.customerService.obterTodosClientes().subscribe({
      next: (customers) => {
        this.managers = this.managers.map((manager) => ({
          ...manager,
          clients: customers.filter(
            (customer: any) => customer.manager && this.normalizeName(customer.manager.name) === this.normalizeName(manager.name)
          ).length,
        }));
      }
    });
  }

  private syncManagersStorage(): void {
    if (typeof localStorage === 'undefined') return;

    this.customerService.obterTodosClientes().subscribe({
      next: (customers) => {
        const payload = this.managers.map((manager) => {
          const cpf = customers.find(c => c.manager && this.normalizeName(c.manager.name) === this.normalizeName(manager.name))?.manager.cpf 
                      || this.generateFallbackCpf(manager.id);
          return {
            nome: manager.name,
            cpf: cpf,
            telefone: manager.phone,
            email: manager.email,
          };
        }) as StoredManager[];
        localStorage.setItem(this.MANAGERS_STORAGE_KEY, JSON.stringify(payload));
      }
    });
  }

  private resolveManagerCpf(manager: ManagerSummary, callback: (cpf: string) => void): void {
    this.customerService.obterTodosClientes().subscribe({
      next: (customers) => {
        const fromCustomers = customers.find(
          (customer: any) => customer.manager && this.normalizeName(customer.manager.name) === this.normalizeName(manager.name)
        )?.manager.cpf;

        if (fromCustomers) {
          callback(fromCustomers);
          return;
        }

        const fromStorage = this.getStoredManagers().find(
          (item) => this.normalizeName(item.nome) === this.normalizeName(manager.name)
        )?.cpf;

        callback(fromStorage || this.generateFallbackCpf(manager.id));
      }
    });
  }

  private getStoredManagers(): StoredManager[] {
    if (typeof localStorage === 'undefined') return [];
    const raw = localStorage.getItem(this.MANAGERS_STORAGE_KEY);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  private generateFallbackCpf(id: number): string {
    const base = 90000000000 + id;
    return String(base).padStart(11, '0').slice(-11);
  }

  private normalizeName(name: string): string {
    if (!name) return '';
    return name
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, ' ')
      .trim();
  }
}
