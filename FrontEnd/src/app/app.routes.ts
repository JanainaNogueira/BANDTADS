import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { Autocadastro } from './User/autocadastro/autocadastro';

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
    }
];
