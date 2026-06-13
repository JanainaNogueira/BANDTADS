import { contaDto } from "./contaDto";
import { enderecoDto } from "./enderecoDto";
import { gerenteDto } from "./gerenteDto";

export interface clienteCompletoDto{
  id?: number;
  cpf: string;
  nome: string;
  telefone?: string;
  email: string;
  endereco: enderecoDto;
  status: string;
  conta: contaDto;
  gerente: gerenteDto;
}