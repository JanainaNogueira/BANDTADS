"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const http_proxy_middleware_1 = require("http-proxy-middleware");
const composition_service_1 = require("../services/composition.service");
const router = (0, express_1.Router)();
const rewriteWithPrefix = (prefix) => (path) => {
    if (path === '/' || path === '') {
        return prefix;
    }
    return `${prefix}${path}`;
};
router.use('/login', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://auth-service:8080', // corrigido: era 8080
    changeOrigin: true,
    pathRewrite: () => '/auth/login',
    logger: console,
    proxyTimeout: 30000,
    timeout: 30000,
}));
router.use('/logout', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://auth-service:8080',
    changeOrigin: true,
    pathRewrite: () => '/auth/logout',
    logger: console,
    onProxyReq: (proxyReq, req) => {
        const auth = req.headers.authorization;
        if (auth) {
            proxyReq.setHeader('Authorization', auth);
        }
    }
}));
router.post('/clientes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    pathRewrite: rewriteWithPrefix('/clientes'),
    logger: console,
}));
// router.post('/gerentes', createProxyMiddleware({
//   target: 'http://saga-service:8080',
//   changeOrigin: true,
//   pathRewrite: rewriteWithPrefix('/gerentes'),
//   logger: console,
// }));
// router.delete('/gerentes/:cpf', createProxyMiddleware({
//   target: 'http://saga-service:8080',
//   changeOrigin: true,
//   pathRewrite: rewriteWithPrefix('/gerentes'),
//   logger: console,
// }));
router.get('/clientes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://cliente-service:8080',
    changeOrigin: true,
    pathRewrite: {
        '^/clientes': '/clientes'
    },
    logger: console,
}));
router.get('/clientes/:id', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://cliente-service:8080',
    changeOrigin: true,
    pathRewrite: rewriteWithPrefix('/clientes'),
    logger: console,
}));
router.get('/gerentes', async (req, res, next) => {
    if (req.query.filtro === 'dashboard') {
        try {
            const resultado = await (0, composition_service_1.buscarDashboardGerentes)();
            return res.json(resultado);
        }
        catch (error) {
            console.error(error.message);
            return res.status(500).json({ error: 'Erro ao compor dashboard' });
        }
    }
    next();
});
router.use('/gerentes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    pathRewrite: rewriteWithPrefix('/gerentes'),
    logger: console,
}));
router.use('/contas', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://conta-service:8080',
    changeOrigin: true,
    pathRewrite: rewriteWithPrefix('/contas'),
    logger: console,
}));
exports.default = router;
