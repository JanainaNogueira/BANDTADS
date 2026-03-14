import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ManagerTopPanel } from '../../components/manager-top-panel/manager-top-panel';
import { ModalRecusar } from './components/modal-recusar/modal-recusar';

@Component({
  selector: 'app-home-gerente',
  imports: [Menu, MatIconModule, CommonModule, ManagerTopPanel, ModalRecusar],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente {
  
  modalAberto = false;

  abrirModal() {
    this.modalAberto = true;
  }

  fecharModal() {
    this.modalAberto = false;
  }
}
