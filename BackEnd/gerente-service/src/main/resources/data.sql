DROP TABLE IF EXISTS gerente_admin;

CREATE TABLE gerente_admin (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(15) NOT NULL,
    tipo VARCHAR(10) NOT NULL
);

INSERT INTO gerente_admin
(nome, cpf, telefone, email, senha, tipo)
VALUES
('Genieve','98574307084','41991087031','ger1@bantads.com.br','tads','GERENTE'),
('Godophredo','64065268052','41991087031','ger2@bantads.com.br','tads','GERENTE'),
('Gyandula','23862179060','41991087031','ger3@bantads.com.br','tads','GERENTE');