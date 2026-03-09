import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-home',
  imports: [Menu, CommonModule, MatIconModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  transactions = [
  {
    name: 'José Almeida',
    date: '12 Mar 2026',
    amount: 530,
    type: 'income'
  },
  {
    name: 'Mercado Central',
    date: '10 Mar 2026',
    amount: 120,
    type: 'expense'
  },
  {
    name: 'Maria Souza',
    date: '08 Mar 2026',
    amount: 250,
    type: 'income'
  },
  {
    name: 'Netflix',
    date: '05 Mar 2026',
    amount: 39.9,
    type: 'expense'
  },
  {
    name: 'Padaria',
    date: '03 Mar 2026',
    amount: 18,
    type: 'expense'
  }
];
}
