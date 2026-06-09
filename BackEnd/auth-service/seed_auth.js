// Script para inserir usuários no MongoDB auth-service
// Password "tads" com SHA-256 + salt "tads"

use("authdb");

// Clear existing users
db.usuarios.deleteMany({});

// Insert test users
db.usuarios.insertMany([
  {
    // admin user
    "_id": ObjectId(),
    "login": "adm1@bantads.com.br",
    "email": "adm1@bantads.com.br",
    "nome": "Administrador Um",
    "cpf": "123.456.789-01",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "ADMIN",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // gerente 1
    "_id": ObjectId(),
    "login": "ger1@bantads.com.br",
    "email": "ger1@bantads.com.br",
    "nome": "Gerente Um",
    "cpf": "123.456.789-02",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "GERENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // gerente 2
    "_id": ObjectId(),
    "login": "ger2@bantads.com.br",
    "email": "ger2@bantads.com.br",
    "nome": "Gerente Dois",
    "cpf": "123.456.789-03",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "GERENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // gerente 3
    "_id": ObjectId(),
    "login": "ger3@bantads.com.br",
    "email": "ger3@bantads.com.br",
    "nome": "Gerente Três",
    "cpf": "123.456.789-04",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "GERENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // cliente 1
    "_id": ObjectId(),
    "login": "cli1@bantads.com.br",
    "email": "cli1@bantads.com.br",
    "nome": "Cliente Um",
    "cpf": "123.456.789-05",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "CLIENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // cliente 2
    "_id": ObjectId(),
    "login": "cli2@bantads.com.br",
    "email": "cli2@bantads.com.br",
    "nome": "Cliente Dois",
    "cpf": "123.456.789-06",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "CLIENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // cliente 3
    "_id": ObjectId(),
    "login": "cli3@bantads.com.br",
    "email": "cli3@bantads.com.br",
    "nome": "Cliente Três",
    "cpf": "123.456.789-07",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "CLIENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // cliente 4
    "_id": ObjectId(),
    "login": "cli4@bantads.com.br",
    "email": "cli4@bantads.com.br",
    "nome": "Cliente Quatro",
    "cpf": "123.456.789-08",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "CLIENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    // cliente 5
    "_id": ObjectId(),
    "login": "cli5@bantads.com.br",
    "email": "cli5@bantads.com.br",
    "nome": "Cliente Cinco",
    "cpf": "123.456.789-09",
    "senha": "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b", // "tads"
    "tipo": "CLIENTE",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);

// Verify
print("Inserted users count: " + db.usuarios.countDocuments());
