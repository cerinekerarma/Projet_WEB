package Client.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/SendServerMessageController")
public class SendServerMessageController extends HttpServlet {

    private static final String API_MESSAGES_ENDPOINT = "http://localhost:8080/Projet_WebServices_war_exploded/api/messages";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("jwt") == null) {
            resp.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }
        String jwt = (String) session.getAttribute("jwt");

        String senderId = req.getParameter("auteurId");  // attention au nom dans le formulaire !
        String serverIdStr = req.getParameter("serverId");
        String contenu = req.getParameter("contenu");

        if (senderId == null || serverIdStr == null || contenu == null || contenu.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants ou invalides");
            return;
        }

        try {
            int serverId = Integer.parseInt(serverIdStr);

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("contenu", contenu.trim());
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            messageMap.put("sendDate", dateStr);

            Map<String, String> auteurMap = new HashMap<>();
            auteurMap.put("id", senderId);
            messageMap.put("auteur", auteurMap);

            messageMap.put("serverId", serverId);

            String jsonPayload = objectMapper.writeValueAsString(messageMap);

            URL url = new URL(API_MESSAGES_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + jwt);

            try (var os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();

            if (status == HttpURLConnection.HTTP_CREATED) {
                resp.sendRedirect("ServerConversationController?serverId=" + serverIdStr);
            } else {
                String errorMsg = "";
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    errorMsg = sb.toString();
                } catch (Exception ignored) {}
                resp.sendError(status, "Erreur API : " + errorMsg);
            }

            connection.disconnect();

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID serveur invalide");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }
}
