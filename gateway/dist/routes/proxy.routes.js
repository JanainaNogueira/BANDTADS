"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const http_proxy_middleware_1 = require("http-proxy-middleware");
const router = (0, express_1.Router)();
router.use('/login', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://auth-service:8080',
    changeOrigin: true,
    pathRewrite: () => '/login',
    logger: console,
}));
router.use('/logout', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://auth-service:8080',
    changeOrigin: true,
    pathRewrite: () => '/logout',
    logger: console,
}));
router.get('/clientes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://cliente-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.get('/clientes/:id', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://cliente-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.use('/gerentes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://gerente-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.use('/contas', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://conta-service:8080',
    changeOrigin: true,
    logger: console,
}));
exports.default = router;
