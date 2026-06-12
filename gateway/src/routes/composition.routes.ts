import { Router } from 'express';

import {
  buscarClienteCompleto,
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

export default router;