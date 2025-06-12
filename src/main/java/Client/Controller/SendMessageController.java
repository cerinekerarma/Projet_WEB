package Client.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/SendMessageController")
public class SendMessageController extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("jwt") == null) {
            resp.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }

        String jwt = (String) session.getAttribute("jwt");

        String senderId = req.getParameter("senderId");
        String receiverId = req.getParameter("receiverId");
        String contenu = req.getParameter("contenu");

        if (senderId == null || receiverId == null || contenu == null || contenu.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants ou invalides");
            return;
        }

        // Préparer la date au format "yyyy-MM-dd"
        String sendDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Construire le JSON à envoyer
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("contenu", contenu);
        messageMap.put("sendDate", sendDate);

        Map<String, String> senderMap = new HashMap<>();
        senderMap.put("id", senderId);
        Map<String, String> receiverMap = new HashMap<>();
        receiverMap.put("id", receiverId);

        messageMap.put("sender", senderMap);
        messageMap.put("receiver", receiverMap);

        String jsonPayload = mapper.writeValueAsString(messageMap);

        // URL de l'API POST
        String apiUrl = "http://localhost:8080/Projet_WebServices_war_exploded/api/messages";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            // Redirige vers la conversation
            resp.sendRedirect("ConversationController?userId=" + receiverId);
        } else {
            String errorMsg;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                errorMsg = sb.toString();
            }
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur API : " + errorMsg);
        }

        conn.disconnect();
    }
}
