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
      name: 'João Silva',
      clients: [
        { name: 'Client A', balance: 500 },
        { name: 'Client B', balance: -200 },
        { name: 'Client C', balance: 0 }
      ]
    },
    {
      name: 'Maria Oliveira',
      clients: [
        { name: 'Client D', balance: 1200 },
        { name: 'Client E', balance: -100 }
      ]
    },
    {
      name: 'Carlos Souza',
      clients: [
        { name: 'Client F', balance: 3000 },
        { name: 'Client G', balance: -500 },
        { name: 'Client H', balance: 150 },
        { name: 'Client I', balance: -80 }
      ]
    },
    {
      name: 'Ana Lima',
      clients: [
        { name: 'Client J', balance: 0 },
        { name: 'Client K', balance: -300 }
      ]
    }
  ];

  processedAdmins: ProcessedAdmin[] = this.processAdmins();

  private processAdmins(): ProcessedAdmin[] {
    return this.admins
      .map((admin) => ({
        name: admin.name,
        totalClients: admin.clients.length,
        totalPositive: admin.clients
          .filter((c) => c.balance >= 0)
          .reduce((acc, c) => acc + c.balance, 0),
        totalNegative: admin.clients
          .filter((c) => c.balance < 0)
          .reduce((acc, c) => acc + c.balance, 0)
      }))
      .sort((a, b) => b.totalPositive - a.totalPositive);
  }

  get totalClients(): number {
    return this.processedAdmins.reduce(
      (acc, admin) => acc + admin.totalClients,
      0
    );
  }
}