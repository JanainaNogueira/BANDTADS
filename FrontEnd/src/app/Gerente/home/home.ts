import { Component } from '@angular/core';
import { MenuGerente } from '../../components/menu-gerente/menu-gerente';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ManagerTopPanel } from '../../components/manager-top-panel/manager-top-panel';

@Component({
  selector: 'app-home-gerente',
  imports: [MenuGerente, MatIconModule, CommonModule, ManagerTopPanel],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente {

}
