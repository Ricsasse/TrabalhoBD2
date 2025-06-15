// backend-livros/routes/authRoutes.js
const express = require('express');
const router = express.Router(); // Cria uma instância do roteador Express
const bcrypt = require('bcryptjs'); // Importa bcryptjs para lidar com senhas
const jwt = require('jsonwebtoken'); // Importa jsonwebtoken para gerar tokens
const Usuario = require('../Models/Usuario'); // Importa o modelo de usuário

// Carrega a chave secreta JWT do arquivo .env
const JWT_SECRET = process.env.JWT_SECRET;

// --- Rota de Registro de Usuário (POST /api/auth/register) ---
router.post('/register', async (req, res) => {
    const { username, email, password } = req.body; // Desestrutura os dados do corpo da requisição

    try {
        // 1. Verifica se já existe um usuário com o mesmo username OU email
        let user = await Usuario.findOne({ $or: [{ username }, { email }] });
        if (user) {
            return res.status(400).json({ message: 'Nome de usuário ou e-mail já registrado.' });
        }

        // 2. Criptografa a senha antes de salvar no banco de dados
        const salt = await bcrypt.genSalt(10); // Gera um "sal" aleatório para o hash (melhora a segurança)
        const passwordHash = await bcrypt.hash(password, salt); // Hasheia a senha usando o sal

        // 3. Cria uma nova instância do modelo Usuario
        user = new Usuario({
            username,
            email,
            passwordHash,
            role: 'user' // Define o papel padrão para novos registros como 'user'
        });

        await user.save(); // Salva o novo usuário no MongoDB

        // Responde com sucesso
        res.status(201).json({ message: 'Usuário registrado com sucesso!', userId: user._id });

    } catch (err) {
        console.error(err.message); // Exibe o erro no console do servidor para depuração
        res.status(500).send('Erro no servidor ao registrar usuário. Tente novamente mais tarde.');
    }
});

// --- Rota de Login de Usuário (POST /api/auth/login) ---
router.post('/login', async (req, res) => {
    const { username, password } = req.body; // Pega o username e a senha do corpo da requisição

    try {
        // 1. Tenta encontrar o usuário no banco de dados pelo username
        const user = await Usuario.findOne({ username });
        if (!user) {
            return res.status(400).json({ message: 'Credenciais inválidas.' }); // Usuário não encontrado
        }

        // 2. Compara a senha fornecida com a senha criptografada armazenada no banco
        const isMatch = await bcrypt.compare(password, user.passwordHash);
        if (!isMatch) {
            return res.status(400).json({ message: 'Credenciais inválidas.' }); // Senha incorreta
        }

        // 3. Se o username e a senha estiverem corretos, gera um JSON Web Token (JWT)
        const payload = {
            user: {
                id: user.id,          // ID único do usuário (do MongoDB)
                username: user.username,
                role: user.role       // O papel do usuário (user ou admin) é importante para autorização futura
            }
        };

        // Assina o token com a chave secreta e define um tempo de expiração
        jwt.sign(
            payload,
            JWT_SECRET,        // A chave secreta carregada do .env
            { expiresIn: '1h' }, // O token será válido por 1 hora
            (err, token) => {
                if (err) throw err; // Se houver um erro na geração do token
                // Retorna o token e outras informações para o frontend
                res.json({ token, message: 'Login bem-sucedido!', user: { id: user.id, username: user.username, role: user.role } });
            }
        );

    } catch (err) {
        console.error(err.message);
        res.status(500).send('Erro no servidor ao fazer login. Tente novamente mais tarde.');
    }
});

module.exports = router; // Exporta o roteador para ser usado no arquivo principal do servidor