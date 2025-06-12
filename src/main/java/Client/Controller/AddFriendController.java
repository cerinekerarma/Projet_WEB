package Client.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;
import java.util.Map;

@WebServlet("/AddFriendController")
public class AddFriendController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("jwt") == null) {
            resp.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }

        String jwt = (String) session.getAttribute("jwt");
        String currentUserId = (String) session.getAttribute("login");
        String friendLogin = req.getParameter("friendUsername");

        try {
            // 1. Récupère tous les utilisateurs
            URL url = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwt);
            conn.setRequestProperty("Accept", "application/json");

            // Debug: Affiche l'URL appelée
            System.out.println("[DEBUG] URL appelée: " + url);

            List<Map<String, Object>> users;
            try (InputStream is = conn.getInputStream()) {
                users = mapper.readValue(is, new TypeReference<>() {});
            }
            conn.disconnect();

            // Debug: Affiche la réponse complète
            System.out.println("[DEBUG] Réponse API: " + users);

            // 2. Cherche l'utilisateur par login (id dans ton cas)
            Map<String, Object> friend = null;
            for (Map<String, Object> user : users) {
                Object userLogin = user.get("id"); // Dans ta réponse JSON, le login est dans "id"
                if (userLogin != null && userLogin.toString().equalsIgnoreCase(friendLogin)) {
                    friend = user;
                    break;
                }
            }

            if (friend == null) {
                System.out.println("[DEBUG] Utilisateur non trouvé dans la liste: " + friendLogin);
                req.setAttribute("error", "Utilisateur non trouvé");
                req.getRequestDispatcher("addFriends.jsp").forward(req, resp);
                return;
            }

            String friendId = friend.get("id").toString();
            System.out.println("[DEBUG] Ami trouvé: " + friendId);

            // 3. Vérifie qu'on ne s'ajoute pas soi-même
            if (friendId.equals(currentUserId)) {
                req.setAttribute("error", "Vous ne pouvez pas vous ajouter vous-même");
                req.getRequestDispatcher("addFriends.jsp").forward(req, resp);
                return;
            }

            // 4. Envoie un message automatique
            String sendDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("contenu", "Bonjour new friend !");
            messageMap.put("sendDate", sendDate);

            Map<String, String> senderMap = new HashMap<>();
            senderMap.put("id", currentUserId);
            Map<String, String> receiverMap = new HashMap<>();
            receiverMap.put("id", friendId);

            messageMap.put("sender", senderMap);
            messageMap.put("receiver", receiverMap);

            String jsonPayload = mapper.writeValueAsString(messageMap);

            URL messageUrl = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/messages");
            HttpURLConnection messageConn = (HttpURLConnection) messageUrl.openConnection();
            messageConn.setRequestMethod("POST");
            messageConn.setRequestProperty("Authorization", "Bearer " + jwt);
            messageConn.setRequestProperty("Content-Type", "application/json");
            messageConn.setDoOutput(true);

            try (OutputStream os = messageConn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            if (messageConn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                req.setAttribute("success", "Ami ajouté avec succès: " + friendId);
                req.setAttribute("friendId", friendId);
            } else {
                String errorMsg = "Erreur lors de l'envoi du message (Code: " + messageConn.getResponseCode() + ")";
                try (BufferedReader br = new BufferedReader(new InputStreamReader(messageConn.getErrorStream()))) {
                    errorMsg += " - " + br.readLine();
                }
                req.setAttribute("error", errorMsg);
            }
            messageConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erreur technique: " + e.getMessage());
        }

        req.getRequestDispatcher("addFriends.jsp").forward(req, resp);
    }
}