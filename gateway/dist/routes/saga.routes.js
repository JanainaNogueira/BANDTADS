"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const http_proxy_middleware_1 = require("http-proxy-middleware");
const router = (0, express_1.Router)();
//autocadastro
router.post('/clientes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
//aprovar gerente
router.put('/clientes/:id/aprovar', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
//rejeitar cliente
router.put('/clientes/:id/rejeitar', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
//alterar perfil cliente
router.put('/clientes/:id', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));

// criar gerente
router.post('/gerentes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));

// remover gerente
router.delete('/gerentes/:cpf', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
exports.default = router;
