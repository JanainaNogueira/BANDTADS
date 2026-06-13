"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const proxy_routes_1 = __importDefault(require("./routes/proxy.routes"));
const saga_routes_1 = __importDefault(require("./routes/saga.routes"));
const composition_routes_1 = __importDefault(require("./routes/composition.routes"));
const reboot_routes_1 = __importDefault(require("./routes/reboot.routes"));
const app = (0, express_1.default)();
const port = 8080;
app.use((0, cors_1.default)());
app.use((req, res, next) => {
    console.log(`[${req.method}] ${req.originalUrl}`);
    next();
});
app.use(reboot_routes_1.default);
app.use(saga_routes_1.default); // saga primeiro
app.use(composition_routes_1.default);
app.use(proxy_routes_1.default); // proxy depois
app.use(express_1.default.json());
app.get('/health', (req, res) => {
    res.json({
        status: 'ok',
        gateway: 'running'
    });
});
app.get('/', (req, res) => {
    res.json({
        services: [
            'auth-service',
            'cliente-service',
            'conta-service',
            'gerente-service',
            'saga-service'
        ]
    });
});
app.use((req, res) => {
    res.status(404).json({
        error: 'Rota não encontrada'
    });
});
app.listen(port, () => {
    console.log(`Gateway rodando na porta ${port}`);
});
