import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ManagerTopPanel } from '../../components/manager-top-panel/manager-top-panel';
import { CustomersHome } from './components/customers-home/customers-home';
import { MOCK_NEW_CUSTOMERS } from '../../../assets/mock/new-customers.mock';

@Component({
  selector: 'app-home-gerente',
  imports: [Menu, MatIconModule, CommonModule, ManagerTopPanel, CustomersHome],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeGerente {

  customers = MOCK_NEW_CUSTOMERS

}
