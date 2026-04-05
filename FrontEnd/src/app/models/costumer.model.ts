import { Manager } from "./manager.model";
import { Status } from "./status-enum.model";

export interface Customer {
  idCliente: string;
  cpf: string;
  name: string;
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