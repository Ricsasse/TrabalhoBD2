// backend-livros/middleware/admin.js
module.exports = function(req, res, next) {
    // O middleware 'auth' já injetou req.user com os dados do usuário do token JWT.
    // Se req.user não existir (o que não deve acontecer se 'auth' rodar primeiro), ou se o role não for 'admin'
    if (!req.user || req.user.role !== 'admin') {
        // Retorna erro 403 Forbidden (Proibido) se o usuário não for um admin
        return res.status(403).json({ message: 'Acesso negado: Você não tem permissões de administrador.' });
    }
    next(); // Se for admin, continua para a próxima função da rota
};