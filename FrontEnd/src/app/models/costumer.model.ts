import { Manager } from "./manager.model";
import { Status } from "./status-enum.model";

export interface Customer {
  id?: number;
  idCliente: string;
  cpf: string;
  name: string;
  nome?: string;
  email: string;
  senha?: string;
  salary: number;
  numberAccount: number;
  balance: number;
  limit: number;
  city?: string;
  state?: string;
  manager: Manager;
  status: Status;
  password?: string;
}