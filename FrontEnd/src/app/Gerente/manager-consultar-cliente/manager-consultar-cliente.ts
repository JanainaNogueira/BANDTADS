import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { CpfPipe } from '../../pipes/cpf.pipe';
import { Menu } from '../../components/menu/menu';
import { ManagerTopPanel } from '../componente/manager-top-panel/manager-top-panel';
import { MOCK_CUSTOMERS } from '../../../assets/mock/customers.mock';
import { Customer } from '../../models/costumer.model';

@Component({
	selector: 'app-manager-consultar-cliente',
	imports: [CommonModule, CurrencyPipe, CpfPipe],
	templateUrl: './manager-consultar-cliente.html',
	styleUrl: './manager-consultar-cliente.css',
})
export class ManagerConsultarCliente {
	cpfDigitado = '';
	cliente: Customer | null = null;
	mensagem = '';

	consultarCliente(): void {
		const cpfNormalizado = this.normalizarCpf(this.cpfDigitado);

		if (!cpfNormalizado) {
			this.cliente = null;
			this.mensagem = 'Informe um CPF para consultar.';
			return;
		}

		const clienteEncontrado = MOCK_CUSTOMERS.find((item) => this.normalizarCpf(item.cpf) === cpfNormalizado);

		if (!clienteEncontrado) {
			this.cliente = null;
			this.mensagem = 'Cliente não encontrado para o CPF informado.';
			return;
		}

		this.cliente = clienteEncontrado;
		this.mensagem = '';
	}

	atualizarCpf(event: Event): void {
		const target = event.target as HTMLInputElement;
		this.cpfDigitado = target.value;
	}

	private normalizarCpf(cpf: string): string {
		return cpf.replace(/\D/g, '');
	}
}
