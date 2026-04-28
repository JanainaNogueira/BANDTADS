export const ENDPOINTS = {
  gerente: {
    listar: '/gerentes',
    criar: '/gerentes',
    atualizar: (id: number) => `/gerentes/${id}`,
    deletar: (id: number) => `/gerentes/${id}`,
    listarGerente: (id: number) => `/gerentes/${id}`
  }
  //colocar os outros aqui
    //cliente...
};