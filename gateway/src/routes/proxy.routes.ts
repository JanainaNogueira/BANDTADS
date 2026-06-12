import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import { buscarDashboardGerentes } from '../services/composition.service';

const router = Router();

const rewriteWithPrefix = (prefix: string) => (path: string) => {
  if (path === '/' || path === '') {
    return prefix;
  }

  return `${prefix}${path}`;
};

router.use('/login', createProxyMiddleware({
  target: 'http://auth-service:8080', // corrigido: era 8080
  changeOrigin: true,
  pathRewrite: () => '/auth/login',
  logger: console,
  proxyTimeout: 30000,  
  timeout: 30000,
}));

router.use('/logout', createProxyMiddleware({
  target: 'http://auth-service:8080',
  changeOrigin: true,
  pathRewrite: () => '/auth/logout',
  logger: console,

  onProxyReq: (proxyReq: any, req: any) => {
    const auth = req.headers.authorization;
    if (auth) {
      proxyReq.setHeader('Authorization', auth);
    }
  }
} as any));

router.post('/clientes', createProxyMiddleware({ // adicionado: POST para saga-service
  target: 'http://saga-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/clientes'),
  logger: console,
}));

router.get('/clientes', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: {
    '^/clientes': '/clientes'
  },
  logger: console,
}));

router.get('/clientes/:id', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/clientes'),
  logger: console,
}));

router.get('/gerentes', async (req, res, next) => {
  if (req.query.filtro === 'dashboard') {
    try {
      const resultado = await buscarDashboardGerentes();
      return res.json(resultado);
    } catch (error: any) {
      console.error(error.message);
      return res.status(500).json({ error: 'Erro ao compor dashboard' });
    }
  }
  next();
});

router.use('/gerentes', createProxyMiddleware({
  target: 'http://gerente-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/gerentes'),
  logger: console,
}));

router.use('/contas', createProxyMiddleware({
  target: 'http://conta-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/contas'),
  logger: console,
}));

export default router;