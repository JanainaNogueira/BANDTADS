import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';

const router = Router();

//autocadastro

router.post('/clientes', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

//aprovar gerente

router.put('/clientes/:id/aprovar', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

//rejeitar cliente

router.put('/clientes/:id/rejeitar', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

//alterar perfil cliente

router.put('/clientes/:id', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

// criar gerente

router.post('/gerentes', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

// remover gerente

router.delete('/gerentes/:id', createProxyMiddleware({

  target: 'http://saga-service:8080',

  changeOrigin: true,

  logger: console,

}));

export default router;