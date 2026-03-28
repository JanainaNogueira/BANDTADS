import { ManagerCreateEditList } from "../../app/models/manager.model";

export type ManagerStatus = 'active' | 'pending' | 'inactive';

export const MOCK_MANAGERS_LIST: ManagerCreateEditList[] = [
  { id: 1, nome: 'Ana Silva', email: 'ana.silva@bantads.com', telefone: '(11) 98765-4321', cpf: '111.111.111-11', clientes: 12, status: 'active', senha: '123456' },
  { id: 2, nome: 'Carlos Mendes', email: 'carlos.mendes@bantads.com', telefone: '(21) 99876-5432', cpf: '222.222.222-22', clientes: 8, status: 'active', senha: '123456' },
  { id: 3, nome: 'Beatriz Santos', email: 'beatriz.santos@bantads.com', telefone: '(31) 91234-5678', cpf: '333.333.333-33', clientes: 15, status: 'active', senha: '123456' },
  { id: 4, nome: 'Diego Oliveira', email: 'diego.oliveira@bantads.com', telefone: '(41) 92345-6789', cpf: '444.444.444-44', clientes: 0, status: 'pending', senha: '123456' },
  { id: 5, nome: 'Fernanda Lima', email: 'fernanda.lima@bantads.com', telefone: '(51) 93456-7890', cpf: '555.555.555-55', clientes: 5, status: 'active', senha: '123456' },
  { id: 6, nome: 'Gabriel Costa', email: 'gabriel.costa@bantads.com', telefone: '(61) 94567-8901', cpf: '666.666.666-66', clientes: 0, status: 'pending', senha: '123456' },
  { id: 7, nome: 'Helena Rocha', email: 'helena.rocha@bantads.com', telefone: '(71) 95678-9012', cpf: '777.777.777-77', clientes: 9, status: 'active', senha: '123456' },
  { id: 8, nome: 'Igor Martins', email: 'igor.martins@bantads.com', telefone: '(81) 96789-0123', cpf: '888.888.888-88', clientes: 0, status: 'inactive', senha: '123456' },
  { id: 9, nome: 'Juliana Ferreira', email: 'juliana.ferreira@bantads.com', telefone: '(91) 97890-1234', cpf: '999.999.999-99', clientes: 11, status: 'active', senha: '123456' },
  { id: 10, nome: 'Lucas Alves', email: 'lucas.alves@bantads.com', telefone: '(11) 98091-2345', cpf: '000.000.000-00', clientes: 0, status: 'pending', senha: '123456' }
];

