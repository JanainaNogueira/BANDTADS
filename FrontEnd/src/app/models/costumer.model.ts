import { Manager } from "./manager.model";

export interface Customer {
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
}