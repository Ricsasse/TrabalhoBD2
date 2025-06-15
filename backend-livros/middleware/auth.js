// backend-livros/middleware/auth.js
const jwt = require('jsonwebtoken');

// Obtém a chave secreta JWT do arquivo .env
const JWT_SECRET = process.env.JWT_SECRET;

module.exports = function(req, res, next) {
    // 1. Tenta obter o token do cabeçalho 'x-auth-token'
    // Este é um padrão comum para enviar tokens de autenticação
    const token = req.header('x-auth-token');

    // 2. Verifica se o token existe
    if (!token) {
        return res.status(401).json({ message: 'Nenhum token, autorização negada.' });
    }

    try {
        // 3. Verifica e decodifica o token
        // jwt.verify retorna o payload (dados do usuário) se o token for válido
        const decoded = jwt.verify(token, JWT_SECRET);

        // 4. Anexa os dados do usuário decodificados (id, username, role) ao objeto 'req'
        // Isso torna as informações do usuário acessíveis nas rotas protegidas
        req.user = decoded.user;
        next(); // Chama a próxima função middleware/rota
    } catch (err) {
        // Se o token for inválido ou expirado
        res.status(401).json({ message: 'Token inválido.' });
    }
};