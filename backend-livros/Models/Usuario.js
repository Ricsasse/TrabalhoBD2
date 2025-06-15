// backend-livros/models/Usuario.js
const mongoose = require('mongoose');

// Define o Schema (esquema) para os documentos de usuário
const UsuarioSchema = new mongoose.Schema({
    username: {
        type: String,
        required: true,      // Campo obrigatório
        unique: true,        // Cada username deve ser único
        trim: true           // Remove espaços em branco antes/depois
    },
    email: {
        type: String,
        required: true,
        unique: true,        // Cada e-mail deve ser único
        trim: true,
        lowercase: true      // Converte o e-mail para minúsculas antes de salvar
    },
    passwordHash: {          // Onde a senha Criptografada será armazenada
        type: String,
        required: true
    },
    role: {                  // Papel do usuário (user ou admin)
        type: String,
        enum: ['user', 'admin'], // Valores permitidos
        default: 'user'      // Valor padrão para novos registros
    },
    createdAt: {             // Data de criação do registro
        type: Date,
        default: Date.now    // Define a data atual como padrão
    }
});

// Exporta o modelo 'Usuario'. Mongoose criará uma coleção chamada 'usuarios' no MongoDB.
module.exports = mongoose.model('Usuario', UsuarioSchema);