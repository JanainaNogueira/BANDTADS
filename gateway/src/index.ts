import express from 'express';
import cors from 'cors';

import proxyRoutes from './routes/proxy.routes';
import sagaRoutes from './routes/saga.routes';
import compositionRoutes from './routes/composition.routes';
import rebootRoutes from './routes/reboot.routes';

const app = express();
const port = 8080;

app.use(cors());

app.use((req, res, next) => {
  console.log(`[${req.method}] ${req.originalUrl}`);
  next();
});

app.use(rebootRoutes);

// proxy.routes NÃO usa express.json() — repassa o body inteiro
app.use(proxyRoutes);

// sagaRoutes e compositionRoutes precisam do express.json()
app.use(express.json());
app.use(sagaRoutes);
app.use(compositionRoutes);

app.get('/health', (req, res) => {

  res.json({
    status: 'ok',
    gateway: 'running'
  });

});


app.get('/', (req, res) => {

  res.json({

    services: [

      'auth-service',
      'cliente-service',
      'conta-service',
      'gerente-service',
      'saga-service'

    ]

  });

});

app.use((req, res) => {

  res.status(404).json({
    error: 'Rota não encontrada'
  });

});

app.listen(port, () => {

  console.log(`Gateway rodando na porta ${port}`);

});