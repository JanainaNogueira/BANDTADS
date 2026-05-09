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
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'home',
        component: Home,
        canActivate: [authGuard]
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
        component: EditProfileComponent,
        canActivate: [authGuard]
    },
    {
        path:'bank-statement',
        component: BankStatementComponent,
        canActivate: [authGuard]
    },
    {
        path:'home-gerente',
        component: HomeGerente,
    },
    {
        path: 'clientes',
        component: AdmCustomers,
        canActivate: [authGuard]
    },
    {
        path: 'gerente-consultar-cliente',
        component: ManagerConsultarCliente,
        canActivate: [authGuard]
    },
    {
        path: 'gerente-clientes',
        component: CustomersPage,
        canActivate: [authGuard]
    },
    {
        path: 'home-admin',
        component: HomeAdm,
        canActivate: [authGuard]
    },
    {
        path: 'adm-gerentes',
        component: AdmManager,
        canActivate: [authGuard]
    },
];
