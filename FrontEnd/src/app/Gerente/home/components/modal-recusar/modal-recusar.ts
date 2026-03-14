import { Component, EventEmitter, Output } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';

@Component({
  selector: 'app-modal-recusar',
  imports: [MatIcon, FormsModule, ReactiveFormsModule, ButtonSubmit],
  templateUrl: './modal-recusar.html',
  styleUrl: './modal-recusar.css',
})
export class ModalRecusar {

  @Output() fechar = new EventEmitter<void>();

  fecharModal() {
    this.fechar.emit();
  }
  
  recusar() {

  }
}
