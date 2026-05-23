"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.buscarClienteCompleto = buscarClienteCompleto;
const axios_1 = __importDefault(require("axios"));
async function buscarClienteCompleto(id) {
    const clienteResponse = await axios_1.default.get(`http://cliente-service:8080/clientes/${id}`);
    const cliente = clienteResponse.data;
    const contaResponse = await axios_1.default.get(`http://conta-service:8080/contas/cliente/${id}`);
    let gerente = null;
    if (cliente.gerenteId) {
        const gerenteResponse = await axios_1.default.get(`http://gerente-service:8080/gerentes/${cliente.gerenteId}`);
        gerente = gerenteResponse.data;
    }
    return {
        cliente,
        conta: contaResponse.data,
        gerente
    };
}
