package Client.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/AuthClientController")
public class AuthClientController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(true);
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
            res.sendError(400, "Login ou mot de passe manquant");
            return;
        }

        // Hashage SHA-256 du mot de passe avant envoi
        String hashedPwd = sha256(password);

        try {
            URL url = new URL("http://localhost:8080/Projet_WebServices_war_exploded/GenererToken");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            // Création de l'objet JSON avec login + hashedPwd
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", hashedPwd);

            String jsonPayload = mapper.writeValueAsString(data);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                // Lire la réponse JSON complète
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonResponse = sb.toString();

                    // Parse JSON pour récupérer le token
                    var node = mapper.readTree(jsonResponse);
                    if (node.has("token")) {
                        String token = node.get("token").asText();
                        // Stocker le token dans la session
                        session.setAttribute("jwt", token);
                    } else {
                        // Pas de token : erreur
                        res.sendRedirect("login.jsp?error=Erreur%20de%20réception%20du%20token");
                        return;
                    }
                }

                session.setAttribute("login", login); // facultatif
                res.sendRedirect("HomeController");   // redirection après succès

            } else {
                // 401 ou autre code => échec login
                res.sendRedirect("login.jsp?error=Login%20ou%20mot%20de%20passe%20incorrect");
            }

        } catch (IOException e) {
            e.printStackTrace();
            res.sendError(500, "Erreur lors de la tentative de connexion : " + e.getMessage());
        }
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
