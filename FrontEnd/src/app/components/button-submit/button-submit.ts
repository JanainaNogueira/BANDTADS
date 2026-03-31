import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import {RouterModule } from '@angular/router';

@Component({
  selector: 'app-button-submit',
  imports: [MatIconModule, RouterModule],
  templateUrl: './button-submit.html',
  styleUrl: './button-submit.css',
})
export class ButtonSubmit {
  @Input() nameButton: string = '';
  @Input() icon: string = '';
  @Input() info: string = '';
  @Input() linkText: string = '';
  @Input() routerLink: string = '';
  @Input() disabled: boolean = false;
  @Output() action = new EventEmitter<void>();

  clickButton(){
    if(!this.disabled){
      this.action.emit();
    }
  }
}
