use bantads_gerente_admin;

create table IF NOT EXISTS GerenteAdmin (
	id int PRIMARY KEY auto_increment NOT NULL,
    nome varchar(100) not null,
	cpf varchar (11) UNIQUE not null,
    telefone VARCHAR(11) NOT NULL,
    email varchar(150) not null,
    senha varchar(15) not null
    tipo varchar(10) not null
);
