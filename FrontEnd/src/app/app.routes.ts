import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { HomeGerente } from './Gerente/home/home';
import { HomeAdm } from './Administrador/home/home-adm.component';
import { Autocadastro } from './User/autocadastro/autocadastro';
import { Login } from './User/login/login';
import { EditProfileComponent } from './User/edit-profile/edit-profile.component';
import { BankStatementComponent } from './User/bank-statement/bank-statement.component';
import { AdmCustomers } from './Administrador/adm-customers/adm-customers';
import { ManagerConsultarCliente } from './Gerente/manager-consultar-cliente/manager-consultar-cliente';
import { CustomersPage } from './Gerente/customers-page/customers-page';
import { AdmManager } from './Administrador/adm-manager/adm-manager';
import { FormManager } from './Administrador/adm-manager/components/form-manager/form-manager';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    {
        path: 'home',
        component: Home
    },
    {
        path: 'autocadastro',
        component: Autocadastro
    },
    {
        path: 'login',
        component: Login
    },
    {
        path: 'editProfile',
        component: EditProfileComponent
    },
    {
        path:'bank-statement',
        component: BankStatementComponent
    },
    {
        path:'home-gerente',
        component: HomeGerente
    },
    {
        path: 'clientes',
        component: AdmCustomers
    },
    {
        path: 'lista-gerentes',
        component: AdmManager
    },
    {
        path: 'gerente-consultar-cliente',
        component: ManagerConsultarCliente
    },
    {
        path: 'gerente-clientes',
        component: CustomersPage
    },
    {
        path: 'home-adm',
        component: HomeAdm
    },
    {
        path: 'adm-gerentes',
        component: AdmManager
    },
];
