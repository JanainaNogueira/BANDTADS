import { Component } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-edit-profile',
  imports: [Menu, ReactiveFormsModule],
  providers: [provideNgxMask()],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent {
  perfilForm: FormGroup;
  isEditMode = false;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.perfilForm = this.fb.group({
      cep: ['', Validators.required],
      rua: [{ value: '', disabled: true }],
      bairro: [{ value: '', disabled: true }],
      numero: ['', Validators.required],
      complemento: [''],
      cidade: [{ value: '', disabled: true }],
      estado: [{ value: '', disabled: true }]
    });
  }

  consultaCEP() {
    const cep = this.perfilForm.get('cep')?.value.replace(/\D/g, '');
    if (!cep || cep.length < 8) {
      return;
    }

    this.http.get(`https://viacep.com.br/ws/${cep}/json`)
      .subscribe((dados: any) => {
        if (!dados.erro) {
          this.perfilForm.patchValue({
            rua: dados.logradouro,
            bairro: dados.bairro,
            cidade: dados.localidade,
            estado: dados.uf
          });
        } else {
          this.perfilForm.patchValue({ cep: '' });
          alert('CEP não encontrado.');
        }
      });
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }
}
