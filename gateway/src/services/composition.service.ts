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

export async function buscarDashboardGerentes() {
  const gerentesResponse = await axios.get(
    'http://gerente-service:8080/gerentes'
  );

  const contasResponse = await axios.get(
    'http://conta-service:8080/contas'
  );

  const gerentes = gerentesResponse.data;
  const contas = contasResponse.data;

  const resultado = gerentes.map((gerente: any) => {
    const clientesGerente = contas.filter(
      (conta: any) => conta.gerenteId === gerente.id
    );

    const saldo_positivo = clientesGerente
      .filter((c: any) => c.saldo > 0)
      .reduce((acc: number, c: any) => acc + Number(c.saldo), 0);

    const saldo_negativo = clientesGerente
      .filter((c: any) => c.saldo < 0)
      .reduce((acc: number, c: any) => acc + Number(c.saldo), 0);

    return {
      gerente: { id: gerente.id, cpf: gerente.cpf, nome: gerente.nome, email: gerente.email, tipo: gerente.tipo, telefone: gerente.telefone },
      clientes: clientesGerente,
      saldo_positivo,
      saldo_negativo
    };
  });

  return resultado.sort((a: any, b: any) => b.saldo_positivo - a.saldo_positivo);
}

export async function buscarRelatorioClientes() {
  const [clientesResponse, contasResponse, gerentesResponse] = await Promise.all([
    axios.get('http://cliente-service:8080/clientes'),
    axios.get('http://conta-service:8080/contas'),
    axios.get('http://gerente-service:8080/gerentes')
  ]);

  const clientes = clientesResponse.data;
  const contas = contasResponse.data;
  const gerentes = gerentesResponse.data;

  return clientes
    .map((cliente: any) => {
      const conta = contas.find((c: any) => c.clienteId === cliente.id);
      const gerenteId = conta ? conta.gerenteId : cliente.gerenteId;
      const gerente = gerentes.find((g: any) => g.id === gerenteId);

      return {
        id: cliente.id,
        cpf: cliente.cpf,
        nome: cliente.nome,
        email: cliente.email,
        salario: cliente.salario,
        conta: conta?.numeroConta || '-',
        saldo: conta?.saldo ?? 0,
        limite: conta?.limite ?? 0,
        gerente: {
          cpf: gerente?.cpf || '-',
          nome: gerente?.nome || '-'
        },
        status: cliente.status
      };
    })
    .sort((a: any, b: any) => (a.nome ?? '').localeCompare(b.nome ?? ''));
}