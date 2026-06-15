import { Router } from 'express';
import jwt from 'jsonwebtoken';

import {
  buscarClienteCompleto,
  buscarDashboardGerentes
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

    console.error(error.message);

    return res.status(500).json({

      error: 'Erro ao compor cliente'

    });

  }

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