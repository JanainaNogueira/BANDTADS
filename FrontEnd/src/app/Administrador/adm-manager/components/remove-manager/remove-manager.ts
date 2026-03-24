import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Customer } from '../../../../models/costumer.model';
import { CustomerService } from '../../../../services/customer.service';

interface ManagerData {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

interface ManagerWithCount extends ManagerData {
  accountCount: number;
}

interface RemoveManagerDialogData {
  managerCpf: string;
  previewOnly?: boolean;
}

interface RemoveManagerResult {
  success: boolean;
  removedManagerCpf: string;
  targetManagerCpf: string;
  movedAccounts: number;
  message: string;
}

@Component({
  selector: 'app-remove-manager',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './remove-manager.html',
  styleUrl: './remove-manager.css',
})
export class RemoveManager {
  private readonly MANAGERS_STORAGE_KEY = 'bantads_managers';

  customers: Customer[] = [];
  managers: ManagerWithCount[] = [];
  selectedManager: ManagerWithCount | null = null;
  targetManager: ManagerWithCount | null = null;

  errorMessage = '';

  constructor(
    private customerService: CustomerService,
    private dialogRef: MatDialogRef<RemoveManager, RemoveManagerResult | null>,
    @Inject(MAT_DIALOG_DATA) public data: RemoveManagerDialogData,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.loadState();
    this.prepareSelection();
  }

  cancelar(): void {
    this.dialogRef.close(null);
  }

  confirmar(): void {
    if (this.data.previewOnly) {
      this.dialogRef.close({
        success: true,
        removedManagerCpf: this.data.managerCpf,
        targetManagerCpf: this.targetManager?.cpf || '',
        movedAccounts: this.selectedManager?.accountCount || 0,
        message: 'Visualização concluída (sem persistir alterações).',
      });
      return;
    }

    const result = this.removeManager(this.data.managerCpf);
    if (result.success) {
      this.dialogRef.close(result);
    }
  }

  private prepareSelection(): void {
    if (!this.canRemoveManager()) {
      this.errorMessage = 'Não é permitido remover o último gerente do banco.';
      return;
    }

    this.selectedManager = this.managers.find(
      (manager) => this.normalizeCpf(manager.cpf) === this.normalizeCpf(this.data.managerCpf)
    ) || null;

    if (!this.selectedManager) {
      this.errorMessage = 'Gerente não encontrado para remoção.';
      return;
    }

    this.targetManager = this.findTargetManager(this.selectedManager.cpf);

    if (!this.targetManager) {
      this.errorMessage = 'Não foi possível identificar gerente destino para as contas.';
    }
  }

  private canRemoveManager(): boolean {
    return this.managers.length > 1;
  }

  private removeManager(managerCpf: string): RemoveManagerResult {
    if (!this.canRemoveManager()) {
      this.errorMessage = 'Não é permitido remover o último gerente do banco.';
      return {
        success: false,
        removedManagerCpf: '',
        targetManagerCpf: '',
        movedAccounts: 0,
        message: this.errorMessage,
      };
    }

    const managerToRemove = this.managers.find(
      (manager) => this.normalizeCpf(manager.cpf) === this.normalizeCpf(managerCpf)
    );

    if (!managerToRemove) {
      this.errorMessage = 'Gerente não encontrado para remoção.';
      return {
        success: false,
        removedManagerCpf: '',
        targetManagerCpf: '',
        movedAccounts: 0,
        message: this.errorMessage,
      };
    }

    const targetManager = this.findTargetManager(managerCpf);
    if (!targetManager) {
      this.errorMessage = 'Não foi possível identificar gerente destino para as contas.';
      return {
        success: false,
        removedManagerCpf: '',
        targetManagerCpf: '',
        movedAccounts: 0,
        message: this.errorMessage,
      };
    }

    let movedAccounts = 0;
    const updatedCustomers = this.customers.map((customer) => {
      if (this.normalizeCpf(customer.manager.cpf) !== this.normalizeCpf(managerCpf)) {
        return customer;
      }

      movedAccounts += 1;
      return {
        ...customer,
        manager: {
          cpf: targetManager.cpf,
          name: targetManager.nome,
        },
      };
    });

    this.customerService.salvarClientes(updatedCustomers);

    const updatedManagers = this.managers
      .filter((manager) => this.normalizeCpf(manager.cpf) !== this.normalizeCpf(managerCpf))
      .map(({ accountCount, ...manager }) => manager);
    this.saveManagers(updatedManagers);

    const message = `Gerente ${managerToRemove.nome} removido. ${movedAccounts} conta(s) atribuída(s) para ${targetManager.nome}.`;

    return {
      success: true,
      removedManagerCpf: managerToRemove.cpf,
      targetManagerCpf: targetManager.cpf,
      movedAccounts,
      message,
    };
  }

  private findTargetManager(removedManagerCpf: string): ManagerWithCount | null {
    const candidates = this.managers.filter(
      (manager) => this.normalizeCpf(manager.cpf) !== this.normalizeCpf(removedManagerCpf)
    );

    if (!candidates.length) {
      return null;
    }

    return [...candidates].sort((a, b) => {
      if (a.accountCount !== b.accountCount) {
        return a.accountCount - b.accountCount;
      }
      return a.nome.localeCompare(b.nome);
    })[0];
  }

  private loadState(): void {
    this.customers = this.customerService.obterTodosClientes();
    const storedManagers = this.getStoredManagers();
    const mergedManagers = this.mergeManagers(storedManagers, this.customers);
    this.saveManagers(mergedManagers);
    this.managers = this.attachAccountCount(mergedManagers, this.customers);
  }

  private mergeManagers(storedManagers: ManagerData[], customers: Customer[]): ManagerData[] {
    const managersByCpf = new Map<string, ManagerData>();

    for (const manager of storedManagers) {
      managersByCpf.set(this.normalizeCpf(manager.cpf), manager);
    }

    for (const customer of customers) {
      const normalizedCpf = this.normalizeCpf(customer.manager.cpf);
      if (!managersByCpf.has(normalizedCpf)) {
        managersByCpf.set(normalizedCpf, {
          nome: customer.manager.name,
          cpf: customer.manager.cpf,
          telefone: '',
          email: this.buildEmail(customer.manager.name),
        });
      }
    }

    return [...managersByCpf.values()].sort((a, b) => a.nome.localeCompare(b.nome));
  }

  private attachAccountCount(managers: ManagerData[], customers: Customer[]): ManagerWithCount[] {
    return managers.map((manager) => ({
      ...manager,
      accountCount: customers.filter(
        (customer) => this.normalizeCpf(customer.manager.cpf) === this.normalizeCpf(manager.cpf)
      ).length,
    }));
  }

  private getStoredManagers(): ManagerData[] {
    if (!isPlatformBrowser(this.platformId)) {
      return [];
    }

    const raw = localStorage.getItem(this.MANAGERS_STORAGE_KEY);
    if (!raw) {
      return [];
    }

    try {
      const parsed = JSON.parse(raw) as Partial<ManagerData>[];
      return parsed
        .filter((manager) => !!manager?.cpf && !!manager?.nome)
        .map((manager) => ({
          nome: manager.nome!,
          cpf: manager.cpf!,
          telefone: manager.telefone || '',
          email: manager.email || this.buildEmail(manager.nome!),
        }));
    } catch {
      return [];
    }
  }

  private saveManagers(managers: ManagerData[]): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    localStorage.setItem(this.MANAGERS_STORAGE_KEY, JSON.stringify(managers));
  }

  private buildEmail(name: string): string {
    const cleanedName = name
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/[^a-z\s]/g, '')
      .trim()
      .replace(/\s+/g, '.');

    return `${cleanedName || 'gerente'}@bantads.com`;
  }

  private normalizeCpf(cpf: string): string {
    return cpf.replace(/\D/g, '');
  }

}
