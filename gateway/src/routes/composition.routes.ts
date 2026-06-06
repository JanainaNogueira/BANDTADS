import { Router } from 'express';

import {
  buscarClienteCompleto,
  buscarDashboardGerentes
} from '../services/composition.service';

const router = Router();

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

router.get('/gerentes', async (req, res) => {

  if (req.query.filtro === 'dashboard') {

      const resultado = await buscarDashboardGerentes();

      return res.json(resultado);
  }

});

export default router;