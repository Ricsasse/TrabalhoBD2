// backend-livros/routes/adminRoutes.js
const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth'); // Middleware de autenticação
const admin = require('../middleware/admin'); // Middleware de autorização de admin
const Usuario = require('../Models/Usuario'); // Modelo de Usuário

// Rota para listar TODOS os usuários
// Acessível apenas para usuários autenticados E que sejam administradores
router.get('/users', auth, admin, async (req, res) => {
    try {
        // Encontra todos os usuários, mas omite o campo passwordHash por segurança
        const users = await Usuario.find().select('-passwordHash');
        res.json(users);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Erro no servidor ao buscar usuários.');
    }
});

// Rota para tornar um usuário existente um administrador (ou remover o admin)
// Acessível apenas para usuários autenticados E que sejam administradores
router.put('/users/:id/role', auth, admin, async (req, res) => {
    const { role } = req.body; // Deve ser 'user' ou 'admin'
    const userId = req.params.id;

    if (!['user', 'admin'].includes(role)) {
        return res.status(400).json({ message: 'Papel (role) inválido. Deve ser "user" ou "admin".' });
    }

    try {
        const user = await Usuario.findById(userId);
        if (!user) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }

        user.role = role;
        await user.save();
        res.json({ message: `Papel do usuário ${user.username} atualizado para ${user.role}.`, user: { id: user._id, username: user.username, role: user.role } });

    } catch (err) {
        console.error(err.message);
        if (err.kind === 'ObjectId') {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        res.status(500).send('Erro no servidor ao atualizar papel do usuário.');
    }
});


module.exports = router;