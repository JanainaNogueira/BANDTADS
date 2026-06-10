import axios from 'axios';

export async function buscarClienteCompleto(id: string) {

  const clienteResponse = await axios.get(

    `http://cliente-service:8080/clientes/${id}`

  );

  const cliente = clienteResponse.data;

  const contaResponse = await axios.get(

    `http://conta-service:8080/contas/cliente/${id}`

  );
  
  let gerente = null;

  if (cliente.gerenteId) {

    const gerenteResponse = await axios.get(

      `http://gerente-service:8080/gerentes/${cliente.gerenteId}`

    );

    gerente = gerenteResponse.data;

  }

  return {

    cliente,

    conta: contaResponse.data,

    gerente

  };

}

export async function buscarClienteCompletoCPF(cpf: string) {

  const clienteResponse = await axios.get(
    `http://cliente-service:8080/clientes/cpf/${cpf}`
  );

  const cliente = clienteResponse.data;

  if (cliente.status === 'REPROVADO') {
    throw new Error('Cliente reprovado');
  }

  const contaResponse = await axios.get(
    `http://conta-service:8080/contas/cliente/${cliente.id}`
  );

  const contas = contaResponse.data;

  return {
    cpf: cliente.cpf,
    nome: cliente.nome,
    email: cliente.email,
    telefone: cliente.telefone,
    conta: contas.length > 0 ? contas[0] : null
  };
}