// backend-livros/server.js
require('dotenv').config(); // Carrega as variáveis de ambiente do arquivo .env
const express = require('express'); // Importa o Express
const mongoose = require('mongoose'); // Importa o Mongoose para interagir com o MongoDB
const cors = require('cors'); // Importa o middleware CORS
const authRoutes = require('./routes/authRoutes'); // Importa as rotas de autenticação
const livroRoutes = require('./routes/livroRoutes'); // Importa as rotas dos livros
const adminRoutes = require('./routes/adminRoutes'); // Importa as rotas do Admin

const app = express(); // Cria uma instância do aplicativo Express
// Define a porta do servidor, usando a variável de ambiente PORT ou 3000 como padrão
const PORT = process.env.PORT || 3000;

// --- Conectar ao MongoDB ---
mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log('MongoDB conectado com sucesso!')) // Mensagem de sucesso na conexão
    .catch(err => console.error('Erro de conexão ao MongoDB:', err)); // Mensagem de erro na conexão

// --- Middlewares (Funções que processam as requisições) ---
// Habilita o parsing de JSON para o corpo das requisições (req.body)
app.use(express.json());
// Habilita o CORS, permitindo que seu frontend se conecte ao backend
app.use(cors());

// --- Rotas da API ---
// Associa as rotas de autenticação (authRoutes) ao prefixo '/api/auth'
// Ex: uma rota `/register` em authRoutes.js será acessada como `/api/auth/register`
app.use('/api/auth', authRoutes);

// Rota de teste simples para verificar se o servidor está online
app.get('/', (req, res) => {
    res.send('API de Gerenciamento de Livros está funcionando!');
});

// --- Iniciar o Servidor ---
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});