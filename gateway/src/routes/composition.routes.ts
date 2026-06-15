import { Router } from 'express';
import jwt from 'jsonwebtoken';

import {
  buscarClienteCompleto,
  buscarClienteCompletoCPF,
  buscarDashboardGerentes,
  buscarRelatorioClientes,
  ClienteNaoEncontradoError
} from '../services/composition.service';

const router = Router();
const JWT_SECRET = process.env.JWT_SECRET || 'bantads-jwt-secret-key-minimo-32-chars';

//cliente
router.get('/clientes/:id', async (req, res) => {

  try {

    const { id } = req.params;

    const resultado = await buscarClienteCompleto(id);

    return res.json(resultado);

  } catch (error: any) {

    if (error instanceof ClienteNaoEncontradoError) {
      return res.status(error.status).json({ error: error.message });
    }

    console.error(error.message);

    return res.status(500).json({

      error: 'Erro ao compor cliente'

    });

  }

});

router.get('/clientes/:cpf', async (req, res) => {

  try {

    const { cpf } = req.params;

    const resultado = await buscarClienteCompletoCPF(cpf);

    return res.json(resultado);

  } catch (error) {

    return res.status(404).json({
      error: 'Cliente não encontrado'
    });

  }
});

router.get('/clientes', async (req, res, next) => {

  if (req.query.filtro === 'adm_relatorio_clientes') {
    const auth = req.headers.authorization;
    if (!auth) return res.status(401).json({ error: 'Token não informado' });
    try {
      const decoded: any = jwt.verify(auth.replace('Bearer ', ''), JWT_SECRET);
      if (decoded.tipo !== 'ADMINISTRADOR') return res.status(403).json({ error: 'Acesso negado' });
    } catch {
      return res.status(401).json({ error: 'Token inválido' });
    }
    try {
      const resultado = await buscarRelatorioClientes();
      return res.json(resultado);
    } catch (error: any) {
      console.error(error.message);
      return res.status(500).json({ error: 'Erro ao compor relatório' });
    }
  }
  next();

});

router.get('/gerentes', async (req, res, next) => {

  if (req.query.filtro === 'dashboard') {
    try {
      const resultado = await buscarDashboardGerentes();
      return res.json(resultado);
    } catch (error: any) {
      console.error(error.message);
      return res.status(500).json({ error: 'Erro ao compor dashboard' });
    }
  }
  next(); 
});

export default router;