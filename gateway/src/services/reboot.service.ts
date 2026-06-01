import axios from 'axios';

export async function executarReboot() {

  await axios.post('http://gerente-service:8080/reboot');

  // Limpa e popula o cliente-service
  await axios.post('http://cliente-service:8080/reboot');

  // Limpa e popula o conta-service
  await axios.post('http://conta-service:8080/reboot');

  // Limpa e popula o auth-service
  await axios.post('http://auth-service:5000/auth/reboot');

  return { status: 'ok' };
}