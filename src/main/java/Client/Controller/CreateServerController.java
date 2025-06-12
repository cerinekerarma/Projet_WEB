package Client.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/CreateServerController")
public class CreateServerController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Affiche le formulaire de création de serveur
        RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jwt = (String) req.getSession().getAttribute("jwt");
        String userId = (String) req.getSession().getAttribute("login");
        String serverName = req.getParameter("serverName");

        if (jwt == null || userId == null || serverName == null || serverName.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Le nom du serveur, le JWT et l'utilisateur sont requis.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        try {
            // Création du serveur via l'API REST
            Map<String, Object> data = new HashMap<>();
            data.put("nom", serverName.trim());

            Map<String, Object> adminMap = new HashMap<>();
            adminMap.put("id", userId);
            data.put("admin", adminMap);

            URL url = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/servers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                objectMapper.writeValue(os, data);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpServletResponse.SC_CREATED) {
                // Récupérer la réponse JSON pour extraire l'id du serveur créé
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Map<String, Object> responseMap = objectMapper.readValue(br, Map.class);
                br.close();

                // L'id du serveur est censé être dans responseMap sous "id"
                Integer serverId = (Integer) responseMap.get("id");
                if (serverId == null) {
                    req.setAttribute("errorMessage", "Impossible de récupérer l'identifiant du serveur créé.");
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
                    dispatcher.forward(req, resp);
                    return;
                }

                // Maintenant intégrer l'utilisateur dans ce serveur (comme dans JoinServerController)
                Map<String, Object> joinData = new HashMap<>();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", userId);

                Map<String, Object> serverMap = new HashMap<>();
                serverMap.put("id", serverId);

                joinData.put("user", userMap);
                joinData.put("server", serverMap);

                URL joinUrl = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/integrer");
                HttpURLConnection joinConn = (HttpURLConnection) joinUrl.openConnection();
                joinConn.setRequestMethod("POST");
                joinConn.setRequestProperty("Authorization", "Bearer " + jwt);
                joinConn.setRequestProperty("Content-Type", "application/json");
                joinConn.setDoOutput(true);

                try (OutputStream os = joinConn.getOutputStream()) {
                    objectMapper.writeValue(os, joinData);
                }

                int joinResponseCode = joinConn.getResponseCode();

                if (joinResponseCode == HttpServletResponse.SC_CREATED) {
                    // Tout est OK, rediriger vers la page du serveur
                    resp.sendRedirect("ServerConversationController?serverId=" + serverId);
                } else {
                    // Erreur lors de l'intégration de l'utilisateur dans le serveur
                    String errorMessage = new String(joinConn.getErrorStream().readAllBytes());
                    req.setAttribute("errorMessage", "Serveur créé mais erreur lors de l'intégration : " + errorMessage);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
                    dispatcher.forward(req, resp);
                }

            } else {
                // Erreur création serveur
                String errorMessage = new String(conn.getErrorStream().readAllBytes());
                req.setAttribute("errorMessage", "Erreur lors de la création du serveur : " + errorMessage);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
                dispatcher.forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Erreur interne : " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/createServer.jsp");
            dispatcher.forward(req, resp);
        }
    }
}
