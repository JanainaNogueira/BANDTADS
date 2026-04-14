const httpProxy = require('express-http-proxy');
const jwt = require('jsonwebtoken');

const authServiceProxy = httpProxy('http://localhost:5000', {
  proxyReqBodyDecorator: function (bodyContent, srcReq) {
    try {
      const retBody = {
        login: bodyContent.user,
        senha: bodyContent.password,
      };
      return retBody;
    } catch (e) {
      console.log('- ERRO: ' + e);
      return bodyContent;
    }
  },

  proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
    proxyReqOpts.headers['Content-Type'] = 'application/json';
    proxyReqOpts.method = 'POST';
    return proxyReqOpts;
  },

  userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
    if (proxyRes.statusCode === 200) {
      const str = Buffer.from(proxyResData).toString('utf-8');
      const objBody = JSON.parse(str);
      const id = objBody.id;

      const token = jwt.sign({ id }, process.env.SECRET, {
        expiresIn: 300,
      });

      userRes.status(200);
      return { auth: true, token: token, data: objBody };
    }

    userRes.status(401);
    return { message: 'Login invalido!' };
  },
});

// Exemplo de uso no gateway:
// app.post('/login', (req, res, next) => {
//   authServiceProxy(req, res, next);
// });

module.exports = authServiceProxy;
