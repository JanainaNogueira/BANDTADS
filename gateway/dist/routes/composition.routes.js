"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const composition_service_1 = require("../services/composition.service");
const router = (0, express_1.Router)();
//cliente
router.get('/clientes/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const resultado = await (0, composition_service_1.buscarClienteCompleto)(id);
        return res.json(resultado);
    }
    catch (error) {
        console.error(error.message);
        return res.status(500).json({
            error: 'Erro ao compor cliente'
        });
    }
});
exports.default = router;
