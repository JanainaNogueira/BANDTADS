import { Conta } from "./conta.model";
import { Endereco } from "./endereco.model";
import { Gerente, Manager } from "./manager.model";
import { Status } from "./status-enum.model";

export interface ClienteCompleto {
  id?: number;
  cpf: string;
  nome: string;
  telefone?: string;
  email: string;
  salario?: number;
  endereco: Endereco;
  status: string;
  conta: Conta;
  gerente: Gerente | null;
}

export interface Customer {
  id?: number;
  idCliente: string;
  cpf: string;
  name: string;
  nome?: string;
  telephone?: string;
  email: string;
  senha?: string;
  salary: number;
  numberAccount: string;
  balance: number;
  limit: number;
  city?: string;
  state?: string;
  manager: Manager;
  status: Status;
  password?: string;
}