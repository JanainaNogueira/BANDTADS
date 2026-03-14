import { Component } from '@angular/core';
import { CustomersList } from '../../components/customers-list/customers-list';
import { CommonModule } from '@angular/common';
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';
import { Menu } from '../../components/menu/menu';

@Component({
  selector: 'app-adm-customers',
  imports: [CommonModule, Menu, CustomersList],
  templateUrl: './adm-customers.html',
  styleUrl: './adm-customers.css',
})

export class AdmCustomers {
  customers = MOCK_CUSTOMERS
  
}
