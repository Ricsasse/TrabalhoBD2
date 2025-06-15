package interface_bd.Api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException; // Para tratar erros de JSON inválido
import com.google.gson.reflect.TypeToken; // Para converter JSON para List<BookData>
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.lang.reflect.Type; // Necessário para TypeToken

import java.util.List; // Importar List

public class ApiService {

    private static ApiService instance;
    private final String BASE_URL = "http://localhost:3000/api"; // Verifique a porta do seu backend
    private String authToken; // Armazena o token JWT após o login

    private ApiService() {
        // Construtor privado para Singleton
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    // Classes de resposta da API
    public static class LoginResponse {
        private String token;
        private String message;
        private String role; // Adicionado para identificar o tipo de usuário

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        public String getRole() {
            return role;
        }
    }

    public static class RegisterResponse {
        private String message;

        public String getMessage() {
            return message;
        }
    }
    
    public static class AddBookResponse {
        private String message;
        @SerializedName("book")
        private BookData bookData;

        public String getMessage() {
            return message;
        }

        public BookData getBookData() {
            return bookData;
        }
    }
    
    // Nova classe para a resposta de sucesso genérica (excluir, atualizar)
    public static class GenericResponse {
        private String message;

        public String getMessage() {
            return message;
        }
    }

    // Classe para representar os dados de um livro (agora com _id)
    public static class BookData {
        private String _id; // ID do livro, vindo do MongoDB
        private String title;
        private String author;
        private String genre;
        private String synopsis;
        private int rating; // 'nota' no seu formulário
        private String status;
        private String comment; // 'comentario' no seu formulário

        // Construtor para envio de dados (sem _id, pois é gerado pelo backend)
        public BookData(String title, String author, String genre, String synopsis, int rating, String status, String comment) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.synopsis = synopsis;
            this.rating = rating;
            this.status = status;
            this.comment = comment;
        }

        // Getters para acessar os dados (se recebidos da API)
        public String get_id() { return _id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getGenre() { return genre; }
        public String getSynopsis() { return synopsis; }
        public int getRating() { return rating; }
        public String getStatus() { return status; }
        public String getComment() { return comment; }
        
        // Setters para atualização de dados (se necessários para construir um objeto BookData para PUT/PATCH)
        public void setTitle(String title) { this.title = title; }
        public void setAuthor(String author) { this.author = author; }
        public void setGenre(String genre) { this.genre = genre; }
        public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
        public void setRating(int rating) { this.rating = rating; }
        public void setStatus(String status) { this.status = status; }
        public void setComment(String comment) { this.comment = comment; }
    }


    // Método para registro de usuário (mantenha como está)
    public RegisterResponse register(String username, String password) throws IOException {
        URL url = new URL(BASE_URL + "/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        return new Gson().fromJson(response.toString(), RegisterResponse.class);
    }

    // Método para login de usuário (mantenha como está)
    public LoginResponse login(String username, String password) throws IOException {
        URL url = new URL(BASE_URL + "/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        
        LoginResponse loginResponse = new Gson().fromJson(response.toString(), LoginResponse.class);
        if (loginResponse != null && loginResponse.getToken() != null) {
            setAuthToken(loginResponse.getToken()); // Armazena o token
        }
        return loginResponse;
    }

    // Método para adicionar um livro (mantenha como está)
    public AddBookResponse addBook(String title, String author, String genre, String synopsis, int rating, String status, String comment) throws IOException {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException("Token de autenticação não disponível. Faça login primeiro.");
        }

        URL url = new URL(BASE_URL + "/books"); // Endpoint para adicionar livros
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + authToken);
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("author", author);
        json.addProperty("genre", genre);
        json.addProperty("synopsis", synopsis);
        json.addProperty("rating", rating);
        json.addProperty("status", status);
        json.addProperty("comment", comment);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        
        return new Gson().fromJson(response.toString(), AddBookResponse.class);
    }
    
    // NOVO MÉTODO: Obter todos os livros do usuário logado
    public List<BookData> getUserBooks() throws IOException {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException("Token de autenticação não disponível. Faça login primeiro.");
        }

        URL url = new URL(BASE_URL + "/books"); // Endpoint para listar livros do usuário
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + authToken); // Envia o token JWT
        conn.setDoOutput(true); // Pode ser false para GET, mas não causa problema

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        
        // Use TypeToken para deserializar uma lista de objetos
        Type bookListType = new TypeToken<List<BookData>>(){}.getType();
        
        List<BookData> books = null;
        try {
            books = new Gson().fromJson(response.toString(), bookListType);
        } catch (JsonSyntaxException e) {
            System.err.println("Erro de sintaxe JSON ao obter livros: " + response.toString());
            throw new IOException("Erro de formato de dados do servidor.", e);
        }
        
        return books;
    }

    // NOVO MÉTODO: Excluir um livro pelo ID
    public GenericResponse deleteBook(String bookId) throws IOException {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException("Token de autenticação não disponível. Faça login primeiro.");
        }
        if (bookId == null || bookId.isEmpty()) {
            throw new IllegalArgumentException("ID do livro não pode ser nulo ou vazio para exclusão.");
        }

        URL url = new URL(BASE_URL + "/books/" + bookId); // Endpoint para excluir um livro específico
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", "Bearer " + authToken); // Envia o token JWT
        conn.setDoOutput(true); // Necessário para métodos que enviam corpo (apesar de DELETE não ter aqui)

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        
        return new Gson().fromJson(response.toString(), GenericResponse.class);
    }
    
    // NOVO MÉTODO: Atualizar um campo específico de um livro
    public GenericResponse updateBookField(String bookId, String fieldName, Object newValue) throws IOException {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException("Token de autenticação não disponível. Faça login primeiro.");
        }
        if (bookId == null || bookId.isEmpty()) {
            throw new IllegalArgumentException("ID do livro não pode ser nulo ou vazio para atualização.");
        }

        URL url = new URL(BASE_URL + "/books/" + bookId); // Endpoint para atualizar um livro específico
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT"); // Ou PATCH, dependendo do seu backend
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + authToken); // Envia o token JWT
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty(fieldName, String.valueOf(newValue)); // Converte o valor para String. Cuidado com tipos!
                                                               // Se o backend espera int para rating, isso precisará ser ajustado.
                                                               // Para `rating` que é int, você enviaria `json.addProperty("rating", (Integer)newValue);`
                                                               // Para simplificar, estamos enviando tudo como String aqui e esperando que o backend converta.
                                                               // Uma abordagem mais robusta seria ter um método `updateBook` que receba um `BookData` completo.

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(
             new InputStreamReader(responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        
        return new Gson().fromJson(response.toString(), GenericResponse.class);
    }
}