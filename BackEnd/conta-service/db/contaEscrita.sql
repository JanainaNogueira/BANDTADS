create database bantads_contaW;

create table conta (
	id SERIAL PRIMARY KEY,
	clienteCpf varchar(11) UNIQUE not null,
	numeroConta int not null,
	saldo double precision not null,
	limite double precision not null,
	gerenteCpf varchar(11) not null,
	dataCriacao date not null
);

create table movimentacoes ( 
	id SERIAL PRIMARY KEY,
	tipo varchar(55) not null,
	valor double precision not null,
	origemCpf varchar(11) not null,
	destinoCpf varchar(11) not null,
	dataHora timestamp not null
);

insert into conta (clienteCpf, numeroConta, saldo, limite, gerenteCpf, dataCriacao) values
('12912861012', 1291, 800.00, 5000.00, '98574307084', '2000-01-01'),
('09506382000', 9500, -10000.00, 10000.00, '64065268052', '1990-10-10'),
('85733854057', 8573, -1000.00, 1500.00, '23862179060', '2012-12-12'),
('58872160006', 5887, 150000.00, 0.00, '98574307084', '2022-02-22'),
('76179646090', 7617, 1500.00, 0.00, '64065268052', '2025-01-01');

insert into movimentacoes (tipo, valor, origemCpf, destinoCpf, dataHora) values
('deposito', 1000.00, '12912861012', '12912861012', '2020-01-01 10:00:00'),
('deposito', 900.00, '12912861012', '12912861012', '2020-01-01 11:00:00'),
('saque', 550.00, '12912861012', '12912861012', '2020-01-01 12:00:00');

