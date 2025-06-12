package Client.Controller;

import Client.POJO.PublierClient;
import Client.POJO.ServerClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@WebServlet("/ServerConversationController")
public class ServerConversationController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("jwt") == null || session.getAttribute("login") == null) {
            res.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }

        String jwt = (String) session.getAttribute("jwt");
        String serverIdParam = req.getParameter("serverId");

        if (serverIdParam == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le paramètre serverId est requis");
            return;
        }

        try {
            int serverId = Integer.parseInt(serverIdParam);

            // 1. Récupérer les infos du serveur
            String serverUrl = "http://localhost:8080/Projet_WebServices_war_exploded/api/servers?id=" + serverId;
            ServerClient server = fetch(serverUrl, jwt, new TypeReference<ServerClient>() {});

            // 2. Récupérer les messages publiés sur ce serveur
            String publierUrl = "http://localhost:8080/Projet_WebServices_war_exploded/api/publier?serverId=" + serverId;
            List<PublierClient> publications = fetchList(publierUrl, jwt, new TypeReference<List<PublierClient>>() {});

            req.setAttribute("server", server);
            req.setAttribute("publications", publications);

            req.getRequestDispatcher("serverConversation.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "serverId doit être un entier");
        } catch (IOException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur de communication avec l'API : " + e.getMessage());
        }
    }

    private <T> T fetch(String apiUrl, String jwt, TypeReference<T> typeRef) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestProperty("Accept", "application/json");

        try (InputStream is = conn.getInputStream()) {
            return mapper.readValue(is, typeRef);
        } finally {
            conn.disconnect();
        }
    }

    private <T> List<T> fetchList(String apiUrl, String jwt, TypeReference<List<T>> typeRef) throws IOException {
        return fetch(apiUrl, jwt, typeRef);
    }
}
