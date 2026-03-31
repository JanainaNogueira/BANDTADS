import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonSubmit } from '../../../../components/button-submit/button-submit';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Customer } from '../customers-home/customers-home';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-modal-recusar',
  imports: [MatIcon, FormsModule, ReactiveFormsModule, ButtonSubmit, CurrencyPipe],
  templateUrl: './modal-recusar.html',
  styleUrl: './modal-recusar.css',
})
export class ModalRecusar {

  @Input() customers: Customer[] = [];
  @Input() customerSelected!: Customer;
  @Output() recusarCliente = new EventEmitter<string>();
  @Output() fechar = new EventEmitter<void>();

  constructor(private snackBar: MatSnackBar) {}

  get filteredCustomer() {
    return this.customers.filter(c => c.cpf === this.customerSelected.cpf);
  }

  fecharModal() {
    this.fechar.emit();
  }

  recusar() {
    this.recusarCliente.emit(this.customerSelected.cpf)
    this.fechar.emit();

    this.snackBar.open('Cliente recusado e removido da visualização!', 'Fechar', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['text-white', 'rounded-3xl']
    });
  }
}
