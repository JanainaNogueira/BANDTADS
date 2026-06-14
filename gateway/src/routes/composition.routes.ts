import { Router } from 'express';

import {
  buscarClienteCompleto,
  buscarClienteCompletoCPF,
  buscarDashboardGerentes,
  ClienteNaoEncontradoError
} from '../services/composition.service';

const router = Router();

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

router.get('/gerentes', async (req, res, next) => {

  if (req.query.filtro === 'dashboard') {

      const resultado = await buscarDashboardGerentes();

      return res.json(resultado);
  }
  next(); 
});

router.get('/gerentes', async (req, res, next) => {

  if (req.query.filtro === 'dashboard') {

      const resultado = await buscarDashboardGerentes();

      return res.json(resultado);
  }
  next(); 
});

export default router;