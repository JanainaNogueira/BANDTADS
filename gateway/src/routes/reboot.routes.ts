import { Router } from 'express';
import { executarReboot } from '../services/reboot.service';

const router = Router();

router.get('/reboot', async (req, res) => {
  try {
    const resultado = await executarReboot();
    return res.status(200).json(resultado);
  } catch (error: any) {
    console.error(error.message);
    return res.status(500).json({ error: 'Erro ao executar reboot' });
  }
});

export default router;