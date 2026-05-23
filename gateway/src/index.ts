import express from 'express';
import cors from 'cors';

import proxyRoutes from './routes/proxy.routes';
import sagaRoutes from './routes/saga.routes';
import compositionRoutes from './routes/composition.routes';

const app = express();

const port = 8080;

app.use(cors());

app.use(express.json());

app.use((req, res, next) => {

  console.log(`[${req.method}] ${req.originalUrl}`);

  next();

});

app.use(proxyRoutes);

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