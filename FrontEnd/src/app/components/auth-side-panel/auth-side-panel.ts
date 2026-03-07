import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Logo } from '../logo/logo';

interface Step {
  title: string;
  description: string;
}

@Component({
  selector: 'app-auth-side-panel',
  imports: [CommonModule, Logo],
  templateUrl: './auth-side-panel.html',
  styleUrl: './auth-side-panel.css',
})

export class AuthSidePanel {
 @Input() title: string = '';
 @Input() subtitle: string = '';
 @Input() about: string = '';
 @Input() steps?: Step[] = [] ;
 @Input() features?: string[] = [];
}