import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { Autocadastro } from './User/autocadastro/autocadastro';
import { Login } from './User/login/login';
import { EditProfileComponent } from './User/edit-profile/edit-profile.component';
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
    }
];
