// backend-livros/routes/livroRoutes.js
const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth'); // Importa o middleware de autenticação
const Livro = require('../Models/Livro'); // Importa o modelo de Livro

// --- Rota para Adicionar um Novo Livro (POST /api/livros) ---
// Protegida pelo middleware 'auth'
router.post('/', auth, async (req, res) => {
    const { titulo, autor, anoPublicacao, genero, nota } = req.body;
    const userId = req.user.id; // O ID do usuário vem do token JWT, injetado pelo middleware 'auth'

    try {
        const novoLivro = new Livro({
            titulo,
            autor,
            anoPublicacao,
            genero,
            nota,
            userId // Associa o livro ao ID do usuário autenticado
        });

        const livroSalvo = await novoLivro.save();
        res.status(201).json({ message: 'Livro adicionado com sucesso!', livro: livroSalvo });
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Erro no servidor ao adicionar livro.');
    }
});

// --- Rota para Listar TODOS os Livros de um Usuário (GET /api/livros/meus) ---
// Protegida pelo middleware 'auth'
router.get('/meus', auth, async (req, res) => {
    const userId = req.user.id; // Pega o ID do usuário autenticado

    try {
        // Encontra todos os livros onde o userId corresponde ao usuário logado
        const livros = await Livro.find({ userId }).sort({ createdAt: -1 }); // Ordena por data de criação
        res.json(livros);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Erro no servidor ao buscar livros.');
    }
});

// --- Rota para Obter um Livro Específico (GET /api/livros/:id) ---
// Protegida pelo middleware 'auth'
router.get('/:id', auth, async (req, res) => {
    try {
        const livro = await Livro.findById(req.params.id);

        if (!livro) {
            return res.status(404).json({ message: 'Livro não encontrado.' });
        }

        // Garante que apenas o proprietário do livro (ou um admin, que faremos depois) possa vê-lo
        if (livro.userId.toString() !== req.user.id && req.user.role !== 'admin') {
            return res.status(403).json({ message: 'Acesso não autorizado a este livro.' });
        }

        res.json(livro);
    } catch (err) {
        console.error(err.message);
        // Se o ID não for um ObjectId válido do MongoDB, Mongoose pode lançar um erro de CastError
        if (err.kind === 'ObjectId') {
            return res.status(404).json({ message: 'Livro não encontrado.' });
        }
        res.status(500).send('Erro no servidor ao buscar o livro.');
    }
});

// --- Rota para Modificar um Livro (PUT /api/livros/:id) ---
// Protegida pelo middleware 'auth'
router.put('/:id', auth, async (req, res) => {
    const { titulo, autor, anoPublicacao, genero, nota } = req.body;
    const livroId = req.params.id;
    const userId = req.user.id;
    const userRole = req.user.role;

    try {
        let livro = await Livro.findById(livroId);

        if (!livro) {
            return res.status(404).json({ message: 'Livro não encontrado para atualização.' });
        }

        // Verifica se o usuário é o proprietário do livro OU um administrador
        if (livro.userId.toString() !== userId && userRole !== 'admin') {
            return res.status(403).json({ message: 'Acesso não autorizado para modificar este livro.' });
        }

        // Atualiza os campos do livro
        livro.titulo = titulo || livro.titulo;
        livro.autor = autor || livro.autor;
        livro.anoPublicacao = anoPublicacao || livro.anoPublicacao;
        livro.genero = genero || livro.genero;
        livro.nota = nota !== undefined ? nota : livro.nota; // Permite nota ser 0 ou null se for o caso

        livro.updatedAt = Date.now(); // Atualiza o timestamp de modificação

        await livro.save(); // Salva as alterações
        res.json({ message: 'Livro atualizado com sucesso!', livro });

    } catch (err) {
        console.error(err.message);
        if (err.kind === 'ObjectId') {
            return res.status(404).json({ message: 'Livro não encontrado.' });
        }
        res.status(500).send('Erro no servidor ao atualizar livro.');
    }
});

// --- Rota para Remover um Livro (DELETE /api/livros/:id) ---
// Protegida pelo middleware 'auth'
router.delete('/:id', auth, async (req, res) => {
    const livroId = req.params.id;
    const userId = req.user.id;
    const userRole = req.user.role;

    try {
        const livro = await Livro.findById(livroId);

        if (!livro) {
            return res.status(404).json({ message: 'Livro não encontrado para exclusão.' });
        }

        // Verifica se o usuário é o proprietário do livro OU um administrador
        if (livro.userId.toString() !== userId && userRole !== 'admin') {
            return res.status(403).json({ message: 'Acesso não autorizado para excluir este livro.' });
        }

        await Livro.deleteOne({ _id: livroId }); // Remove o livro

        res.json({ message: 'Livro removido com sucesso!' });
    } catch (err) {
        console.error(err.message);
        if (err.kind === 'ObjectId') {
            return res.status(404).json({ message: 'Livro não encontrado.' });
        }
        res.status(500).send('Erro no servidor ao remover livro.');
    }
});

module.exports = router;