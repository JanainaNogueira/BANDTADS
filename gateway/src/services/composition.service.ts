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

export async function buscarClienteCompletoEmail(email: string) {

  const clienteResponse = await axios.get(
    `http://cliente-service:8080/clientes/email/${email}`
  );

  const cliente = clienteResponse.data;

  const contaResponse = await axios.get(
    `http://conta-service:8080/contas/cliente/${cliente.id}`
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