import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { Login } from './User/login/login';
import { BankStatementComponent } from './User/bank-statement/bank-statement.component';

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
        path: 'login',
        component: Login
    },
    {
        path:'bank-statement',
        component: BankStatementComponent
    }
];
