-- Insert test clients into PostgreSQL
INSERT INTO clientes (email, nome, cpf, rua, estado, cep, status) VALUES
  ('cli1@bantads.com.br', 'Cliente Um', '123.456.789-05', 'Rua A', 'SP', '12345-678', 'ATIVO'),
  ('cli2@bantads.com.br', 'Cliente Dois', '123.456.789-06', 'Rua B', 'SP', '12346-678', 'ATIVO'),
  ('cli3@bantads.com.br', 'Cliente Três', '123.456.789-07', 'Rua C', 'RJ', '12347-678', 'ATIVO'),
  ('cli4@bantads.com.br', 'Cliente Quatro', '123.456.789-08', 'Rua D', 'MG', '12348-678', 'ATIVO'),
  ('cli5@bantads.com.br', 'Cliente Cinco', '123.456.789-09', 'Rua E', 'BA', '12349-678', 'ATIVO');

-- Get client IDs and insert accounts
INSERT INTO contas (cliente_id, numero_conta, data_criacao, saldo, limite, gerente_id) 
SELECT id, id::text || '291', NOW(), 1000.00, 500.00, 1 FROM clientes;
