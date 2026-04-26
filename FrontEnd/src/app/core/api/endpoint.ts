export const ENDPOINTS = {
  gerente: {
    listar: '/gerentes',
    criar: '/gerentes',
    atualizar: (cpf: string) => `/gerentes/${cpf}`,
    deletar: (cpf: string) => `/gerentes/${cpf}`,
    listarGerente: (cpf: string) => `/gerentes/${cpf}`
  }
  //colocar os outros aqui
    //cliente...
};