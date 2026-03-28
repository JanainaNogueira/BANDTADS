import { ManagerStatus } from "../../assets/mock/managers.mock";

export interface ManagerSummary {
  id: number;
  name: string;
  email: string;
  phone: string;
  clients: number;
  status: ManagerStatus;
}

export interface Manager {
  name: string;
  cpf: string;
}

export interface ManagerCreateEdit{
  name: string;
  cpf?: string;
  telefone?: string;
  email: string;
  senha: string;
}


