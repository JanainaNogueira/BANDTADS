import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { CpfPipe } from '../../pipes/cpf.pipe';
import { Menu } from '../../components/menu/menu';
import { ManagerTopPanel } from '../componente/manager-top-panel/manager-top-panel';
import { Customer } from '../../models/costumer.model';
import { CustomerService } from '../../services/customer.service';

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

	constructor(private customerService: CustomerService) {}

	consultarCliente(): void {
		const cpfNormalizado = this.normalizarCpf(this.cpfDigitado);

		if (!cpfNormalizado) {
			this.cliente = null;
			this.mensagem = 'Informe um CPF para consultar.';
			return;
		}

		// tenta consultar via API e mostra mensagem apropriada
		this.customerService.obterTodosClientesApi().subscribe({
			next: (clientes) => {
				const encontrado = clientes.find((item) => this.normalizarCpf(item.cpf) === cpfNormalizado);
				if (!encontrado) {
					this.cliente = null;
					this.mensagem = 'Cliente não encontrado para o CPF informado.';
					return;
				}

				this.cliente = encontrado;
				this.mensagem = '';
			},
			error: () => {
				this.cliente = null;
				this.mensagem = 'Erro ao consultar o serviço. Tente novamente.';
			}
		});
	}

	atualizarCpf(event: Event): void {
		const target = event.target as HTMLInputElement;
		this.cpfDigitado = target.value;
	}

	private normalizarCpf(cpf: string): string {
		return cpf.replace(/\D/g, '');
	}
}
