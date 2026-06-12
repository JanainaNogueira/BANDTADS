import { Router } from 'express';

import {
  buscarClienteCompleto,
  buscarClienteCompletoEmail
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

router.get('/clientes/:email', async (req, res) => {

  try {

    const { email } = req.params;

    const resultado = await buscarClienteCompletoEmail(email);

    return res.json(resultado);

  } catch (error: any) {

    console.error(error.message);

    return res.status(500).json({

      error: 'Erro ao compor cliente'

    });

  }

});


export default router;