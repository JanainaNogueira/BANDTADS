import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-form-title',
  imports: [],
  templateUrl: './form-title.html',
  styleUrl: './form-title.css',
})
export class FormTitle {
  @Input() tag?: string 
  @Input() title!: string 
  @Input() label!: string 
}
