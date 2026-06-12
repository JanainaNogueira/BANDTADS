import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import jwt from 'jsonwebtoken';
import { buscarClienteCompletoEmail } from '../services/composition.service';

const router = Router();

const rewriteWithPrefix = (prefix: string) => (path: string) => {
  if (path === '/' || path === '') {
    return prefix;
  }

  return `${prefix}${path}`;
};

const JWT_SECRET = process.env.JWT_SECRET || 'bantads-jwt-secret-key-minimo-32-chars';

const injectUserType = (proxyReq: any, req: any) => {
  const auth = req.headers.authorization;
  if (auth) {
    try {
      const token = auth.replace('Bearer ', '');
      const decoded: any = jwt.verify(token, JWT_SECRET);
      proxyReq.setHeader('X-User-Tipo', decoded.tipo);
      proxyReq.setHeader('X-User-Email', decoded.email);
    } catch (e) {
      // token inválido — deixa passar, o serviço decide
    }
  }
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
  pathRewrite: {
    '^/clientes': '/clientes'
  },
  logger: console,
}));

router.get('/clientes', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: {
    '^/clientes': '/clientes'
  },
  logger: console,
  on: {
    proxyReq: injectUserType
  }
}));

router.get('/clientes/:id', createProxyMiddleware({
  target: 'http://cliente-service:8080',
  changeOrigin: true,
  pathRewrite: {
    '^/clientes': '/clientes'
  },
  logger: console,
}));

router.get('/clientes/email/:email', async (req, res) => {
  const resultado = await buscarClienteCompletoEmail(req.params.email);
  return res.json(resultado);
});

router.use(
  '/gerentes',
  createProxyMiddleware({
    target: 'http://gerente-service:8080',
    changeOrigin: true,
    pathRewrite: (path) => {
      return path === '/' ? '/gerentes' : `/gerentes${path}`;
    },
    logger: console,
  })
);

router.use('/contas', createProxyMiddleware({
  target: 'http://conta-service:8080',
  changeOrigin: true,
  pathRewrite: rewriteWithPrefix('/contas'),
  logger: console,
}));

export default router;