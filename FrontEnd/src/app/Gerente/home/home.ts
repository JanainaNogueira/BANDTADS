import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ManagerTopPanel } from '../../components/manager-top-panel/manager-top-panel';
import { ModalRecusar } from './components/modal-recusar/modal-recusar';
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';
import { CustomersHome } from './components/customers-home/customers-home';

@Component({
  selector: 'app-home-gerente',
  imports: [Menu, MatIconModule, CommonModule, ManagerTopPanel, ModalRecusar, CustomersHome],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente {

  customers = MOCK_CUSTOMERS

}
