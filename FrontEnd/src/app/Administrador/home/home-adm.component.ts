import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Menu } from '../../components/menu/menu';
import { CompositionService } from '../../services/composition.service';
import { HttpClientModule } from '@angular/common/http';

interface Client {
  name: string;
  balance: number;
}

interface Admin {
  name: string;
  clients: Client[];
}

interface ProcessedAdmin {
  name: string;
  totalClients: number;
  totalPositive: number;
  totalNegative: number;
}

@Component({
  selector: 'app-home-adm',
  standalone: true,
  imports: [CommonModule, MatIconModule, Menu, HttpClientModule],
  templateUrl: './home-adm.component.html',
  styleUrls: ['./home-adm.component.css']
})
export class HomeAdm implements OnInit {

  processedAdmins: ProcessedAdmin[] = [];

  constructor(private compositionService: CompositionService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.compositionService.getDashboard().subscribe({
      next: (items) => {
        this.processedAdmins = items.map((it: any) => {
          const gerente = it.gerente;
          const clientes = it.clientes || [];

          const totalPositive = clientes
            .filter((c: any) => (c.balance ?? 0) >= 0)
            .reduce((acc: number, c: any) => acc + (c.balance ?? 0), 0);

          const totalNegativeRaw = clientes
            .filter((c: any) => (c.balance ?? 0) < 0)
            .reduce((acc: number, c: any) => acc + (c.balance ?? 0), 0);

          return {
            name: gerente.nome,
            totalClients: clientes.length,
            totalPositive,
            totalNegative: Math.abs(totalNegativeRaw)
          } as ProcessedAdmin;
        });
      },
      error: () => {
        this.processedAdmins = [];
      }
    });
  }


  getInitials(name: string): string {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}