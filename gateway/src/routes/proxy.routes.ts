import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';

const router = Router();

const rewriteWithPrefix = (prefix: string) => (path: string) => {
  if (path === '/' || path === '') {
    return prefix;
  }

  return `${prefix}${path}`;
};

router.use('/login', createProxyMiddleware({
  target: 'http://auth-service:5000', // corrigido: era 8080
  changeOrigin: true,
  pathRewrite: () => '/auth/login',
  logger: console,
  proxyTimeout: 30000,  
  timeout: 30000,
}));

router.use('/logout', createProxyMiddleware({
  target: 'http://auth-service:5000', // corrigido: era 8080
  changeOrigin: true,
  pathRewrite: () => '/auth/logout',
  logger: console,
  proxyTimeout: 30000,  
  timeout: 30000,
}));

router.post('/clientes', createProxyMiddleware({ // adicionado: POST para saga-service
  target: 'http://saga-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/clientes'),
  logger: console,
}));

router.get('/clientes', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/clientes'),
  logger: console,
}));

router.get('/clientes/:id', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/clientes'),
  logger: console,
}));

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