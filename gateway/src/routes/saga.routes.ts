import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import express from 'express';
import axios from 'axios';

const router = Router();

router.post('/clientes', createProxyMiddleware({
  target: 'http://saga-service:8080',
  changeOrigin: true,
  logger: console,
}));

router.put('/clientes/:id/aprovar', createProxyMiddleware({
  target: 'http://saga-service:8080',
  changeOrigin: true,
  logger: console,
}));

router.put('/clientes/:id/rejeitar', createProxyMiddleware({
  target: 'http://saga-service:8080',
  changeOrigin: true,
  logger: console,
}));

router.put('/clientes/:id', createProxyMiddleware({
  target: 'http://saga-service:8080',
  changeOrigin: true,
  logger: console,
}));

router.post('/gerentes', express.json(), async (req, res) => {
   console.log("PASSOU PELA SAGA");
  try {
    const response = await axios.post(
      'http://saga-service:8080/gerentes',
      req.body,
      { headers: { Authorization: req.headers.authorization } }
    );
    return res.status(response.status).json(response.data);
  } catch (error: any) {
    return res.status(error.response?.status || 500).json(error.response?.data);
  }
});

router.delete('/gerentes/:cpf', async (req, res) => {
  try {
    const response = await axios.delete(
      `http://saga-service:8080/gerentes/${req.params.cpf}`,
      { headers: { Authorization: req.headers.authorization } }
    );
    return res.status(response.status).json(response.data);
  } catch (error: any) {
    return res.status(error.response?.status || 500).json(error.response?.data);
  }
});

export default router;