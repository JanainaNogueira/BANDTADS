import axios, { AxiosError } from 'axios';

export class ClienteNaoEncontradoError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export async function buscarClienteCompleto(id: string) {

  let clienteData: any;
  try {
    const clienteResponse = await axios.get(
      `http://cliente-service:8080/clientes/${id}`
    );
    clienteData = clienteResponse.data;
  } catch (err: any) {
    const status = err?.response?.status ?? 500;
    throw new ClienteNaoEncontradoError(status, `cliente-service retornou ${status} para id=${id}`);
  }

  // O clienteId do modelo é um Integer; o `id` recebido pode ser CPF (11 dígitos)
  const clienteId: number | string = clienteData.id ?? id;

  let contaData: any = null;
  // a conta é criada de forma assíncrona após a aprovação; tenta até 3x com backoff
  const delays = [0, 300, 800];
  for (const delay of delays) {
    if (delay > 0) await new Promise(r => setTimeout(r, delay));
    try {
      const contaResponse = await axios.get(
        `http://conta-service:8080/contas/cliente/${clienteId}`
      );
      contaData = contaResponse.data;
      break;
    } catch {
      // conta ainda não existe — tenta de novo
    }
  }

  let gerenteData: any = null;
  if (clienteData.gerenteId) {
    try {
      const gerenteResponse = await axios.get(
        `http://gerente-service:8080/gerentes/${clienteData.gerenteId}`
      );
      gerenteData = gerenteResponse.data;
    } catch {
      // gerente não encontrado — ignora
    }
  }

  // /contas/cliente/{id} retorna List<LerContaDTO> — pega o primeiro elemento
  const conta0 = Array.isArray(contaData) ? contaData[0] : contaData;

  // resposta plana que o teste espera: r["cpf"], r["conta"], r["limite"], r["saldo"], r["gerente"]
  return {
    ...clienteData,
    conta:    conta0?.numeroConta ?? null,
    saldo:    conta0?.saldo       ?? null,
    limite:   conta0?.limite      ?? null,
    gerente:  gerenteData?.cpf    ?? null,
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