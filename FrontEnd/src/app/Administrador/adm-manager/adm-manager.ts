import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormManager } from './components/form-manager/form-manager';
import { Menu } from '../../components/menu/menu';
import { MOCK_MANAGERS } from '../../../assets/mock/manegers.mock';
import { ManagerCreateEdit } from '../../models/manager.model';
import {  MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-adm-manager',
  imports: [Menu, CommonModule, MatDialogModule, MatIconModule],
  templateUrl: './adm-manager.html',
  styleUrl: './adm-manager.css',
})
export class AdmManager {
  constructor(private dialog: MatDialog) {}
   mockGerente: ManagerCreateEdit = MOCK_MANAGERS;


  abrirModalCriar(): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: { modo: 'criar' }
    });

    dialogRef.afterClosed().subscribe((dados) => {
      if (dados) {
        console.log('Criar gerente:', dados);
      }
    });
  }

  abrirModalEditar(gerente: ManagerCreateEdit): void {
    const dialogRef = this.dialog.open(FormManager, {
      width: '760px',
      maxWidth: '96vw',
      data: {
        modo: 'editar',
        gerente: this.mockGerente
      }
    });

    dialogRef.afterClosed().subscribe((dados) => {
      if (dados) {
        console.log('Editar gerente:', dados);
      }
    });
  }
}
