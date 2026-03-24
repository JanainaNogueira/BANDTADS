import { Customer } from "../../app/models/costumer.model";

export const MOCK_CUSTOMERS: Customer[] = [
  {
    cpf: '12345678901',
    name: 'Ana Souza',
    email: 'ana@email.com',
    salary: 5500,
    numberAccount: 1001,
    balance: 8200,
    limit: 3000,
    city: 'Curitiba',
    state: 'PR',
    manager: { cpf: '11122233344', name: 'Carlos Mendes' }
  },
  {
    cpf: '12345678902',
    name: 'Bruno Lima',
    email: 'bruno@email.com',
    salary: 4200,
    numberAccount: 1002,
    balance: -1500,
    limit: 2000,
    city: 'São Paulo',
    state: 'SP',
    manager: { cpf: '55566677788', name: 'Fernanda Alves' }
  },
  {
    cpf: '12345678903',
    name: 'Carla Rocha',
    email: 'carla@email.com',
    salary: 6100,
    numberAccount: 1003,
    balance: 12000,
    limit: 5000,
    city: 'Belo Horizonte',
    state: 'MG',
    manager: { cpf: '11122233344', name: 'Carlos Mendes' }
  },
  {
    cpf: '12345678904',
    name: 'Daniel Alves',
    email: 'daniel@email.com',
    salary: 3000,
    numberAccount: 1004,
    balance: 800,
    limit: 1500,
    city: 'Porto Alegre',
    state: 'RS',
    manager: { cpf: '99988877766', name: 'Juliana Costa' }
  },
  {
    cpf: '12345678905',
    name: 'Eduarda Martins',
    email: 'eduarda@email.com',
    salary: 7200,
    numberAccount: 1005,
    balance: 15000,
    limit: 7000,
    city: 'Florianópolis',
    state: 'SC',
    manager: { cpf: '55566677788', name: 'Fernanda Alves' }
  },
  {
    cpf: '12345678906',
    name: 'Felipe Gomes',
    email: 'felipe@email.com',
    salary: 2800,
    numberAccount: 1006,
    balance: -500,
    limit: 1000,
    city: 'Curitiba',
    state: 'PR',
    manager: { cpf: '99988877766', name: 'Juliana Costa' }
  },
  {
    cpf: '12345678907',
    name: 'Gabriela Silva',
    email: 'gabriela@email.com',
    salary: 4500,
    numberAccount: 1007,
    balance: 3000,
    limit: 2500,
    city: 'São Paulo',
    state: 'SP',
    manager: { cpf: '11122233344', name: 'Carlos Mendes' }
  },
  {
    cpf: '12345678908',
    name: 'Henrique Dias',
    email: 'henrique@email.com',
    salary: 3900,
    numberAccount: 1008,
    balance: 1200,
    limit: 1800,
    city: 'Rio de Janeiro',
    state: 'RJ',
    manager: { cpf: '55566677788', name: 'Fernanda Alves' }
  },
  {
    cpf: '12345678909',
    name: 'Isabela Freitas',
    email: 'isabela@email.com',
    salary: 5100,
    numberAccount: 1009,
    balance: 6400,
    limit: 3200,
    city: 'Salvador',
    state: 'BA',
    manager: { cpf: '99988877766', name: 'Juliana Costa' }
  },
  {
    cpf: '12345678910',
    name: 'João Pedro',
    email: 'joao@email.com',
    salary: 4700,
    numberAccount: 1010,
    balance: 2100,
    limit: 2000,
    city: 'Fortaleza',
    state: 'CE',
    manager: { cpf: '11122233344', name: 'Carlos Mendes' }
  }
];