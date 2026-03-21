import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormManager } from './components/form-manager/form-manager';
import { Menu } from '../../components/menu/menu';

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager {
  constructor(private dialog: MatDialog) {}

  formMaager(): void {
    this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw'
    });
  }
}
