import { Component } from '@angular/core';
import { MenuActions } from '../menu-actions/menu-actions';

@Component({
  selector: 'app-menu',
  imports: [MenuActions],
  templateUrl: './menu.html',
  styleUrl: './menu.css',
})
export class Menu {

  getInitials(name: string): string {
  return name
    .split(' ')
    .map(n => n[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();
}
}
