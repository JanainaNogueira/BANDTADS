"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const http_proxy_middleware_1 = require("http-proxy-middleware");
const express_2 = __importDefault(require("express"));
const axios_1 = __importDefault(require("axios"));
const router = (0, express_1.Router)();
router.post('/clientes', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.put('/clientes/:id/aprovar', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.put('/clientes/:id/rejeitar', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.put('/clientes/:id', (0, http_proxy_middleware_1.createProxyMiddleware)({
    target: 'http://saga-service:8080',
    changeOrigin: true,
    logger: console,
}));
router.post('/gerentes', express_2.default.json(), async (req, res) => {
    console.log("PASSOU PELA SAGA");
    try {
        const response = await axios_1.default.post('http://saga-service:8080/gerentes', req.body, { headers: { Authorization: req.headers.authorization } });
        return res.status(response.status).json(response.data);
    }
    catch (error) {
        return res.status(error.response?.status || 500).json(error.response?.data);
    }
});
router.delete('/gerentes/:cpf', async (req, res) => {
    try {
        const response = await axios_1.default.delete(`http://saga-service:8080/gerentes/${req.params.cpf}`, { headers: { Authorization: req.headers.authorization } });
        return res.status(response.status).json(response.data);
    }
    catch (error) {
        return res.status(error.response?.status || 500).json(error.response?.data);
    }
});
exports.default = router;
