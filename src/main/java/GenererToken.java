import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/GenererToken")
public class GenererToken extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/UsersDB";
        String user = "discord_user";
        String password = "discord_password";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC non trouvé", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        // Lecture et parsing JSON
        Map<String, String> jsonMap;
        try {
            jsonMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {});
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("""
            {
              "status": "error",
              "message": "JSON invalide"
            }
            """);
            return;
        }

        String login = jsonMap.get("login");
        String password = jsonMap.get("password");

        System.out.println(login);
        System.out.println(password);

        if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("""
            {
              "status": "error",
              "message": "Login ou mot de passe manquant"
            }
            """);
            return;
        }

        try (Connection conn = getConnection()) {
            // Vérification avec mot de passe SHA-256 en hexa
            String sql = "SELECT * FROM \"User\" WHERE login = ? AND password = decode(?, 'hex')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String token = JwtManager.createJWT(login);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("""
                {
                  "status": "success",
                  "message": "Connexion réussie",
                  "token": "%s"
                }
                """.formatted(token));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("""
                {
                  "status": "fail",
                  "message": "Login ou mot de passe incorrect"
                }
                """);
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("""
            {
              "status": "error",
              "message": "Erreur SQL",
              "details": "%s"
            }
            """.formatted(e.getMessage().replace("\"", "'")));
        }
    }
}
