"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.rebootAllServices = rebootAllServices;
const axios_1 = __importDefault(require("axios"));
const AUTH_URL = process.env.AUTH_URL || "http://auth-service:8080/auth";
const CLIENTE_URL = process.env.CLIENTE_URL || "http://cliente-service:8080";
const CONTA_URL = process.env.CONTA_URL || "http://conta-service:8080";
const GERENTE_URL = process.env.GERENTE_URL || "http://gerente-service:8080";
async function rebootAllServices() {
    await Promise.all([
        axios_1.default.get(`${AUTH_URL}/reboot`),
        axios_1.default.get(`${CLIENTE_URL}/reboot`),
        axios_1.default.get(`${CONTA_URL}/reboot`),
        axios_1.default.get(`${GERENTE_URL}/reboot`)
    ]);
}
