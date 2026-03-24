import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Menu } from '../../components/menu/menu';

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
  imports: [CommonModule, MatIconModule, Menu],
  templateUrl: './home-adm.component.html',
  styleUrl: './home-adm.component.css'
})
export class HomeAdm {

  private admins: Admin[] = [
    {
      name: 'Ana Silva',
      clients: [
        { name: 'Cliente A', balance: 500 },
        { name: 'Cliente B', balance: -200 },
        { name: 'Cliente C', balance: 0 }
      ]
    },
    {
      name: 'Carlos Souza',
      clients: [
        { name: 'Cliente D', balance: 1200 },
        { name: 'Cliente E', balance: -100 }
      ]
    },
    {
      name: 'Maria Oliveira',
      clients: [
        { name: 'Cliente F', balance: 3000 },
        { name: 'Cliente G', balance: -500 }
      ]
    },
    {
      name: 'João Silva',
      clients: [
        { name: 'Cliente H', balance: 150 },
        { name: 'Cliente I', balance: -80 }
      ]
    }
  ];

  processedAdmins: ProcessedAdmin[] = this.processAdmins();

  private processAdmins(): ProcessedAdmin[] {
    return this.admins
      .map((admin) => {
        const totalPositive = admin.clients
          .filter((c) => c.balance >= 0)
          .reduce((acc, c) => acc + c.balance, 0);

        const totalNegativeRaw = admin.clients
          .filter((c) => c.balance < 0)
          .reduce((acc, c) => acc + c.balance, 0);

        return {
          name: admin.name,
          totalClients: admin.clients.length,
          totalPositive,
          totalNegative: Math.abs(totalNegativeRaw)
        };
      })
      .sort((a, b) => b.totalPositive - a.totalPositive);
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}