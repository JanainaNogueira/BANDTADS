import { Component, Input } from '@angular/core';
import {MatIconModule} from '@angular/material/icon'
import { RouterLink, RouterLinkActive } from "@angular/router";

@Component({
  selector: 'app-menu-actions',
  imports: [MatIconModule, RouterLink, RouterLinkActive],
  templateUrl: './menu-actions.html',
  styleUrl: './menu-actions.css',
})
export class MenuActions {
  @Input() icon!:string
  @Input() label!: string 
  @Input() routerLink!: string | any[]
}
