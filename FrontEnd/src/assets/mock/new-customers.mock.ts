import { Customer } from "../../app/components/customers-list/customers-list";

const MANAGERS = [
  {
    cpf: '98574307084',
    name: 'Geniéve'
  },
  {
    cpf: '64065268052',
    name: 'Godophredo'
  },
  {
    cpf: '23862179060',
    name: 'Gyândula'
  }
];

export const MOCK_NEW_CUSTOMERS: Customer[] = [
  {
    cpf: '10293847566',
    name: 'Juliana Rocha',
    email: 'juliana.rocha@email.com',
    salary: 6800,
    numberAccount: 2001,
    balance: 12500,
    limit: 4000,
    city: 'Curitiba',
    state: 'PR',
    manager: MANAGERS[0]
  },
  {
    cpf: '56473829100',
    name: 'Felipe Martins',
    email: 'felipe.martins@email.com',
    salary: 3900,
    numberAccount: 2002,
    balance: -800,
    limit: 1500,
    city: 'Florianópolis',
    state: 'SC',
    manager: MANAGERS[1]
  },
  {
    cpf: '84736251499',
    name: 'Camila Fernandes',
    email: 'camila.fernandes@email.com',
    salary: 7200,
    numberAccount: 2003,
    balance: 5400,
    limit: 3500,
    city: 'São Paulo',
    state: 'SP',
    manager: MANAGERS[2]
  },
  {
    cpf: '91827364500',
    name: 'Rafael Oliveira',
    email: 'rafael.oliveira@email.com',
    salary: 5100,
    numberAccount: 2004,
    balance: 2300,
    limit: 2500,
    city: 'Belo Horizonte',
    state: 'MG',
    manager: MANAGERS[0]
  },
  {
    cpf: '19283746555',
    name: 'Larissa Costa',
    email: 'larissa.costa@email.com',
    salary: 4600,
    numberAccount: 2005,
    balance: -1200,
    limit: 2000,
    city: 'Porto Alegre',
    state: 'RS',
    manager: MANAGERS[1]
  },
  {
    cpf: '67584930211',
    name: 'Gabriel Souza',
    email: 'gabriel.souza@email.com',
    salary: 8300,
    numberAccount: 2006,
    balance: 15400,
    limit: 5000,
    city: 'Rio de Janeiro',
    state: 'RJ',
    manager: MANAGERS[2]
  },
  {
    cpf: '33445566778',
    name: 'Aline Batista',
    email: 'aline.batista@email.com',
    salary: 3700,
    numberAccount: 2007,
    balance: 900,
    limit: 1200,
    city: 'Salvador',
    state: 'BA',
    manager: MANAGERS[0]
  },
  {
    cpf: '88997766554',
    name: 'Diego Carvalho',
    email: 'diego.carvalho@email.com',
    salary: 6100,
    numberAccount: 2008,
    balance: 7800,
    limit: 3000,
    city: 'Fortaleza',
    state: 'CE',
    manager: MANAGERS[1]
  },
  {
    cpf: '55667788990',
    name: 'Renata Lopes',
    email: 'renata.lopes@email.com',
    salary: 4800,
    numberAccount: 2009,
    balance: -300,
    limit: 1800,
    city: 'Recife',
    state: 'PE',
    manager: MANAGERS[2]
  },
  {
    cpf: '22334455667',
    name: 'Lucas Pereira',
    email: 'lucas.pereira@email.com',
    salary: 5500,
    numberAccount: 2010,
    balance: 4200,
    limit: 2500,
    city: 'Brasília',
    state: 'DF',
    manager: MANAGERS[0]
  }
];