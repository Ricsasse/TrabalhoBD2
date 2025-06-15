// backend-livros/models/Livro.js
const mongoose = require('mongoose');

const LivroSchema = new mongoose.Schema({
    titulo: {
        type: String,
        required: true,
        trim: true
    },
    autor: {
        type: String,
        required: true,
        trim: true
    },
    anoPublicacao: {
        type: Number,
        required: true
    },
    genero: {
        type: String,
        trim: true
    },
    nota: { // Nota dada pelo usuário (de 1 a 5, por exemplo)
        type: Number,
        min: 1,
        max: 5,
        required: true // A nota é obrigatória na criação
    },
    // O campo userId é fundamental para vincular o livro ao usuário que o criou
    userId: {
        type: mongoose.Schema.Types.ObjectId, // Tipo especial para IDs do MongoDB
        ref: 'Usuario', // Faz referência ao modelo 'Usuario'
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now
    },
    updatedAt: {
        type: Date,
        default: Date.now
    }
});

// Antes de salvar, atualiza o campo 'updatedAt'
LivroSchema.pre('save', function(next) {
    this.updatedAt = Date.now();
    next();
});

// Cria o modelo 'Livro'
module.exports = mongoose.model('Livro', LivroSchema);