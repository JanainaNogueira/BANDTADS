import { Component } from '@angular/core';
import { MenuGerente } from '../../components/menu-gerente/menu-gerente';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-home',
  imports: [MenuGerente, MatIconModule, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente {

}
