import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-tabs-operacao',
  imports: [CommonModule, MatIconModule, RouterLink, RouterLinkActive],
  templateUrl: './tabs-operacao.html',
  styleUrl: './tabs-operacao.css',
})
export class TabsOperacao {
  @Input() saldoAtual: number = 0;

  tabs = [
    { label: 'Transferência', icon: 'people_outline', rota: '/transferir' },
    { label: 'Saque', icon: 'shield', rota: '/sacar' },
    { label: 'Depositar', icon: 'notifications_none', rota: '/depositar' },
  ];
}
