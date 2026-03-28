import { ManagerStatus } from "../../assets/mock/managers.mock";

export interface Manager {
  name: string;
  cpf: string;
}

export interface ManagerCreateEditList {
  id: number;         
  nome: string;       
  email: string;      
  telefone: string;   
  cpf: string;        
  senha: string;      
  status?: ManagerStatus; 
  clientes?: number;  
}


