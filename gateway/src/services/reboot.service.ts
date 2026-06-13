import axios from "axios";

const AUTH_URL = process.env.AUTH_URL || "http://auth-service:8080/auth";
const CLIENTE_URL = process.env.CLIENTE_URL || "http://cliente-service:8080";
const CONTA_URL = process.env.CONTA_URL || "http://conta-service:8080";
const GERENTE_URL = process.env.GERENTE_URL || "http://gerente-service:8080";

export async function rebootAllServices() {
  await Promise.all([
    axios.get(`${AUTH_URL}/reboot`),
    axios.get(`${CLIENTE_URL}/reboot`),
    axios.get(`${GERENTE_URL}/reboot`),
    axios.get(`${CONTA_URL}/reboot`)
  ]);
}