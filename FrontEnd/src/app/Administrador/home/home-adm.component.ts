import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Menu } from '../../components/menu/menu';
import { CompositionService } from '../../services/composition.services';

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
export class HomeAdm implements OnInit {

  processedAdmins: ProcessedAdmin[] = [];
  carregando = true;

  constructor(private compositionService: CompositionService) {}

  ngOnInit(): void {
    this.carregando = true;
    this.compositionService.getDashboard().subscribe({
      next: (data) => {
        this.processedAdmins = data.sort((a, b) => b.totalPositive - a.totalPositive);
        this.carregando = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dashboard', err);
        this.carregando = false;
      }
    });
  }

  getInitials(name: string): string {
    if (!name) return '??';
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}