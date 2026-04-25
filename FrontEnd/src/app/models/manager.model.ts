export interface Manager {
  name: string;
  cpf: string;
}

export interface ManagerSummary {
  id: number;
  name: string;
  email: string;
  phone: string;
  clients?: number;
  status: ManagerStatus;
  cpf?: string;
}

export interface ManagerCreateEdit{
  id: number,
  name: string;
  cpf?: string;
  telefone?: string;
  email: string;
  senha: string;
}

export type ManagerStatus = 'active' | 'inactive';
