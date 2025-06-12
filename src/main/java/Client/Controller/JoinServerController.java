package Client.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/JoinServerController")
public class JoinServerController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Affiche le formulaire
        RequestDispatcher dispatcher = req.getRequestDispatcher("/joinServer.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jwt = (String) req.getSession().getAttribute("jwt");
        String userId = (String) req.getSession().getAttribute("login");
        String serverIdStr = req.getParameter("serverId");

        if (jwt == null || userId == null || serverIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT, user ID et server ID sont requis.");
            return;
        }

        int serverId;
        try {
            serverId = Integer.parseInt(serverIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'identifiant du serveur est invalide.");
            return;
        }

        try {
            // ✅ Construction de la structure JSON attendue
            Map<String, Object> data = new HashMap<>();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", userId);

            Map<String, Object> serverMap = new HashMap<>();
            serverMap.put("id", serverId);

            data.put("user", userMap);
            data.put("server", serverMap);

            // ✅ Appel POST à l'API REST
            URL url = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/integrer");
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
                resp.sendRedirect("ServerConversationController?serverId=" + serverId);
            } else {
                req.setAttribute("errorMessage", "Le serveur est introuvable ou est déjà rejoint.");
                RequestDispatcher dispatcher = req.getRequestDispatcher("/joinServer.jsp");
                dispatcher.forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Une erreur interne est survenue : " + e.getMessage());
        }
    }
}
