"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const reboot_service_1 = require("../services/reboot.service");
const router = (0, express_1.Router)();
router.get("/reboot", async (req, res) => {
    try {
        await (0, reboot_service_1.rebootAllServices)();
        return res.status(200).send();
    }
    catch (error) {
        console.error("Erro no reboot:", error);
        return res.status(500).send();
    }
});
exports.default = router;
