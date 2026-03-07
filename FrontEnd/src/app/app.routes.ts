import { Routes } from '@angular/router';
import { Home } from './User/home/home';
import { Login } from './User/login/login';

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
    }
];
