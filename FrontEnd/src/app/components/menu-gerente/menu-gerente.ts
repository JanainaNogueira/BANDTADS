import { Component } from '@angular/core';
import { MenuActions } from '../menu-actions/menu-actions';

@Component({
  selector: 'app-menu-gerente',
  imports: [MenuActions],
  templateUrl: './menu-gerente.html',
  styleUrl: './menu-gerente.css',
})
export class MenuGerente {

  getInitials(name: string): string {
  return name
    .split(' ')
    .map(n => n[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();
}
}
