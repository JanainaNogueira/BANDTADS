import axios from 'axios';
import { clienteCompletoDto } from '../dtos/clienteCompletoDto';

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

export async function buscarClienteCompletoEmail(email: string): Promise<clienteCompletoDto> {

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
    id: cliente.id,
    cpf: cliente.cpf,
    nome: cliente.nome,
    telefone: cliente.telefone,
    email: cliente.email,
    endereco: cliente.endereco,
    status: cliente.status,
    conta: contaResponse.data,
    gerente: gerente
  };
}