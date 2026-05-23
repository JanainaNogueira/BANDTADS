create database bantads_gerenteadmin;

create table gerenteadmin (
    id SERIAL PRIMARY KEY,
    cpf varchar(11) UNIQUE not null,
    nome varchar(100) not null,
    email varchar(150) not null,
    tipo varchar(55) not null
);