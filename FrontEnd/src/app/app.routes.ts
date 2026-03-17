import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { HomeGerente } from './Gerente/home/home';
import { HomeAdm } from './UserAdm/home-adm/home-adm.component';
import { Autocadastro } from './User/autocadastro/autocadastro';
import { Login } from './User/login/login';
import { EditProfileComponent } from './User/edit-profile/edit-profile.component';
import { BankStatementComponent } from './User/bank-statement/bank-statement.component';
import { Depositar } from './User/depositar/depositar';
import { AdmCustomers } from './UserAdm/adm-customers/adm-customers';

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
        path: 'depositar',
        component: Depositar
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
        path: 'home-adm',
        component: HomeAdm
    }
];
