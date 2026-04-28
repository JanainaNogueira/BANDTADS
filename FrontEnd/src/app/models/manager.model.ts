export interface Manager {
  name: string;
  cpf: string;
}

export interface Gerente {
  id: number;
  nome: string;
  email: string;
  telefone: string;
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

export interface AdicionarGerenteDTO{
  nome: string,
  cpf: string,
  email: string,
  telefone: string,
  senha: string
}

export interface LerGerenteDTO{
  nome: string,
  cpf: string,
  email: string,
  telefone: string
}

export interface EditaGerenteDTO{
  nome: string,
  email: string,
  telefone: string,
  senha: string
}

export type ManagerStatus = 'active' | 'inactive';
