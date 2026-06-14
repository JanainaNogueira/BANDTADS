# CONTEXTO_PROJETO_BANTADS

> Documento de contexto para análise, implementação e validação do projeto BANTADS.
>
> Toda sugestão, implementação ou correção deve seguir:
>
> - Especificação oficial UFPR (DS152 - DAC)
> - Rubrica de avaliação
> - Casos de teste oficiais
> - Arquitetura de Microsserviços
>
> O objetivo é atingir conceito **EXCELENTE** em todos os critérios da rubrica.

---

# OBJETIVO DO PROJETO

Implementar o sistema BANTADS (Internet Banking) utilizando arquitetura de microsserviços.

O sistema possui três perfis:

- Cliente
- Gerente
- Administrador

---

# STACK OBRIGATÓRIA

## Frontend

- Angular

## API Gateway

- Node.js

## Backend

- Spring Boot

## Banco de Dados

- PostgreSQL
- MongoDB

## Mensageria

- RabbitMQ

## Containerização

- Docker

---

# ESTRUTURA ATUAL DO PROJETO

## Root

Arquivos principais:

- docker-compose.yml
- README.md
- package.json

---

## FrontEnd

Aplicação Angular.

Arquivos importantes:

- nginx.conf
- dockerfile
- package.json

### Build

```bash
npm run build
```

Resultado:

```text
/dist/FrontEnd
```

### Deploy

Aplicação servida por Nginx.

### Observação importante

O Angular utiliza prerender.

As rotas são geradas como:

```text
/home/index.html
/login/index.html
/dashboard/index.html
```

O Nginx deve utilizar:

```nginx
try_files $uri $uri/index.html $uri/ /index.html;
```

---

## Gateway

Tecnologia:

- Node.js
- TypeScript

Estrutura:

```text
gateway/
└── src/
```

Responsabilidades:

- Roteamento
- Proxy reverso
- JWT
- Controle de acesso
- Ponto único de entrada

O frontend nunca deve acessar microsserviços diretamente.

---

## BackEnd

### auth-service

Responsável por:

- Login
- Logout
- JWT
- Usuários

Porta:

```text
8081
```

---

### cliente-service

Responsável por:

- Cadastro
- Perfil
- Dados do cliente

Porta:

```text
8082
```

---

### conta-service

Responsável por:

- Conta
- Saldo
- Limite
- Extrato
- Depósito
- Saque
- Transferência

Porta:

```text
8083
```

IMPORTANTE:

Este microsserviço deve implementar CQRS.

---

### gerente-service

Responsável por:

- Aprovação
- Rejeição
- Dashboard
- CRUD de gerentes

Porta:

```text
8084
```

---

### saga-service

Responsável por:

- Orquestração das SAGAs

Porta:

```text
8090
```

---

# INFRAESTRUTURA

## MongoDB Auth

Porta:

```text
27018 -> 27017
```

---

## MongoDB Conta

Porta:

```text
27019 -> 27017
```

---

## RabbitMQ

Portas:

```text
5672
15672
```

Responsável por:

- Eventos
- CQRS
- SAGA
- Comunicação assíncrona

---

# PORTAS

| Serviço    | Host  | Container |
| ----------- | ----- | --------- |
| Frontend    | 4200  | 80        |
| Gateway     | 8080  | 8080      |
| Auth        | 8081  | 8080      |
| Cliente     | 8082  | 8080      |
| Conta       | 8083  | 8080      |
| Gerente     | 8084  | 8080      |
| Saga        | 8090  | 8080      |
| Mongo Auth  | 27018 | 27017     |
| Mongo Conta | 27019 | 27017     |
| RabbitMQ    | 5672  | 5672      |
| RabbitMQ UI | 15672 | 15672     |

---

# EXECUÇÃO

## Build completo

```bash
docker compose up -d --build
```

## Apenas frontend

```bash
docker compose up -d --build frontend
```

## Ver containers

```bash
docker compose ps
```

## Logs frontend

```bash
docker compose logs --tail 200 frontend
```

---

# REQUISITOS FUNCIONAIS

## CLIENTE

### R01 - Autocadastro

- CPF único
- Não permitir duplicidade
- Cliente entra em fila de aprovação
- Conta não é criada imediatamente
- Senha não é enviada imediatamente

---

### R02 - Login / Logout

Login utilizando:

- e-mail
- senha

Autenticação via JWT.

---

### R03 - Tela Inicial

Exibir:

- saldo atual
- menu

Saldo negativo:

- vermelho
- sinal negativo

---

### R04 - Alteração de Perfil

Pode alterar:

- nome
- e-mail
- endereço
- telefone
- salário

Não pode alterar:

- CPF

Ao alterar salário:

- recalcular limite

Se novo limite ficar abaixo do saldo negativo atual:

- limite = saldo negativo atual

---

### R05 - Depósito

- Apenas na própria conta
- Atualizar saldo

---

### R06 - Saque

Permitir somente se:

```text
saldo + limite >= valor
```

---

### R07 - Transferência

Registrar:

- origem
- destino
- data
- valor

---

### R08 - Extrato

Exibir:

- data/hora
- tipo
- origem
- destino
- valor

Saídas:

- vermelho

Entradas:

- azul

Exibir saldo consolidado por dia.

---

# GERENTE

### R09

Visualizar clientes pendentes.

---

### R10

Aprovar cliente.

Ao aprovar:

- criar conta
- gerar número de conta (4 dígitos)
- gerar senha
- enviar e-mail

Limite:

```text
salário >= 2000

limite = salário / 2
```

Caso contrário:

```text
limite = 0
```

---

### R11

Rejeitar cliente.

Obrigatório:

- motivo
- e-mail
- data/hora

---

### R12

Listar clientes.

Ordenação:

- nome crescente

Pesquisa:

- CPF parcial
- Nome parcial

---

### R13

Consultar cliente.

Exibir:

- dados pessoais
- saldo
- limite

---

### R14

Top 3 clientes.

Ordenação:

- saldo decrescente

---

# ADMINISTRADOR

### R15

Dashboard.

Exibir por gerente:

- quantidade de clientes
- total saldo positivo
- total saldo negativo

Ordenação:

- maior saldo positivo primeiro

---

### R16

Relatório de clientes.

Exibir:

- CPF cliente
- Nome cliente
- E-mail
- Salário
- Conta
- Saldo
- Limite
- CPF gerente
- Nome gerente

Ordenação:

- nome crescente

---

### R17

Inserção de gerente.

Novo gerente recebe cliente do gerente com mais clientes.

Desempate:

- menor saldo positivo

---

### R18

Remoção de gerente.

Redistribuir clientes para gerente com menos clientes.

Não permitir remover último gerente.

---

### R19

Listagem de gerentes.

Exibir:

- Nome
- CPF
- E-mail
- Telefone

Ordenação:

- nome crescente

---

### R20

Alteração de gerente.

Permitir:

- nome
- e-mail
- senha

---

# ARQUITETURA OBRIGATÓRIA

## API Gateway

Fluxo obrigatório:

```text
Frontend
    ↓
API Gateway
    ↓
Microsserviços
```

Proibido:

```text
Frontend
    ↓
Microsserviço
```

---

## Database Per Service

Cada serviço possui:

- banco próprio
  ou
- schema próprio

Nenhum microsserviço pode acessar banco de outro.

---

## Schema Per Service

Obrigatório.

---

## CQRS

Obrigatório no Conta Service.

### Write Model

- depósito
- saque
- transferência

### Read Model

- saldo
- extrato
- consultas

Sincronização:

- RabbitMQ

---

# SAGAS OBRIGATÓRIAS

## Autocadastro

Fluxo:

1. Cliente cria cadastro
2. Auth cria usuário
3. Conta identifica gerente
4. Gerente associa responsável

Compensações obrigatórias.

---

## Alteração de Perfil

Fluxo:

1. Atualizar cliente
2. Atualizar limite

Compensações obrigatórias.

---

## Inserção de Gerente

Fluxo:

1. Criar gerente
2. Redistribuir clientes

Compensações obrigatórias.

---

## Remoção de Gerente

Fluxo:

1. Encontrar gerente destino
2. Transferir clientes
3. Remover gerente

Compensações obrigatórias.

---

# RUBRICA - CRITÉRIOS PARA EXCELENTE

## SAGA

Necessário:

- Orquestrador centralizado
- Fluxo explícito
- Compensações implementadas
- RabbitMQ
- Tratamento de falhas

---

## API COMPOSITION

Necessário:

- Agregar múltiplos serviços
- Chamadas eficientes
- Tratamento de timeout
- Fallback
- Respostas parciais

---

## BANCO DE DADOS

Necessário:

- Schema per Service
- Database per Service
- DDD
- Modelagem autônoma
- Consistência dos dados

---

## COMUNICAÇÃO

Necessário:

- RabbitMQ
- Assíncrona
- Desacoplada
- Tratamento de erros

---

# CASOS DE TESTE OFICIAIS

## Cadastro e Login

- R01.1
- R01.2
- R01.3
- R02.1
- R02.2
- R02.3
- R02.4
- R02.5

## Aprovação

- R09.1
- R10.1
- R10.2
- R11.1
- R11.2

## Cliente

- R03.1
- R04.1
- R04.2
- R05.1
- R05.2
- R06.1
- R06.2
- R07.1
- R07.2
- R07.3
- R08.1
- R08.2

## Gerente

- R12.1
- R12.2
- R13.1
- R14.1

## Administrador

- R15.1
- R15.2
- R16.1
- R16.2
- R17.1
- R17.2
- R17.3
- R18.1
- R18.2
- R19.1
- R20.1
- R20.2

---

# STATUS ATUAL

## Implementado

- R19.1 Listagem de Gerentes

## Em Desenvolvimento

Atualizar conforme evolução do projeto.

---

# INSTRUÇÕES PARA A IA

Ao receber código, documentação ou dúvidas:

1. Identificar requisito afetado.
2. Identificar caso de teste afetado.
3. Identificar item da rubrica afetado.
4. Comparar com especificação oficial.
5. Priorizar conceito EXCELENTE.
6. Não sugerir soluções que violem:
   - Microsserviços
   - API Gateway
   - CQRS
   - RabbitMQ
   - SAGA Orquestrada
   - Database Per Service
   - Schema Per Service

Sempre informar:

- O que está correto.
- O que está faltando.
- O que precisa ser ajustado.
- Impacto na rubrica.
- Impacto nos testes.
