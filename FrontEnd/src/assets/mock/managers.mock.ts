import { ManagerCreateEdit, ManagerSummary } from "../../app/models/manager.model";

export type ManagerStatus = 'active' | 'pending' | 'inactive';

export const MOCK_MANAGERS_LIST: ManagerSummary[] = [
  { id: 1, name: 'Ana Silva', email: 'ana.silva@bantads.com', phone: '(11) 98765-4321', clients: 12, status: 'active' },
  { id: 2, name: 'Carlos Mendes', email: 'carlos.mendes@bantads.com', phone: '(21) 99876-5432', clients: 8, status: 'active' },
  { id: 3, name: 'Beatriz Santos', email: 'beatriz.santos@bantads.com', phone: '(31) 91234-5678', clients: 15, status: 'active' },
  { id: 4, name: 'Diego Oliveira', email: 'diego.oliveira@bantads.com', phone: '(41) 92345-6789', clients: 0, status: 'pending' },
  { id: 5, name: 'Fernanda Lima', email: 'fernanda.lima@bantads.com', phone: '(51) 93456-7890', clients: 5, status: 'active' },
  { id: 6, name: 'Gabriel Costa', email: 'gabriel.costa@bantads.com', phone: '(61) 94567-8901', clients: 0, status: 'pending' },
  { id: 7, name: 'Helena Rocha', email: 'helena.rocha@bantads.com', phone: '(71) 95678-9012', clients: 9, status: 'active' },
  { id: 8, name: 'Igor Martins', email: 'igor.martins@bantads.com', phone: '(81) 96789-0123', clients: 0, status: 'inactive' },
  { id: 9, name: 'Juliana Ferreira', email: 'juliana.ferreira@bantads.com', phone: '(91) 97890-1234', clients: 11, status: 'active' },
  { id: 10, name: 'Lucas Alves', email: 'lucas.alves@bantads.com', phone: '(11) 98091-2345', clients: 0, status: 'pending' }
];

export const MOCK_MANAGERS_CREATE: ManagerCreateEdit= 
  {
    id:1,
    name: 'Maria Silva',
    cpf: '123.456.789-00',
    telefone: '(41) 99999-9999',
    email: 'maria@email.com',
    senha: 'oioioi'
  }

export const MOCK_MANAGERS: ManagerCreateEdit[]=[
  {
    id:2,
    name: 'Gerente',
    email: 'gerente@email.com',
    senha: '123'
  },
  {
    id:3,
    name: 'Maria Silva',
    cpf: '123.456.789-00',
    telefone: '(41) 99999-9999',
    email: 'maria@email.com',
    senha: '123'
  }
] 
