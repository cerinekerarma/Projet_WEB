package Client.Controller;

import Client.POJO.EcrireClient;
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

@WebServlet("/ConversationController")
public class ConversationController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("jwt") == null || session.getAttribute("login") == null) {
            res.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }

        String login = (String) session.getAttribute("login");
        String jwt = (String) session.getAttribute("jwt");

        String interlocuteurId = req.getParameter("userId");
        if (interlocuteurId == null || interlocuteurId.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le paramètre userId est requis");
            return;
        }

        try {
            // Construire l’URL API avec les deux utilisateurs pour récupérer la conversation entre eux
            String apiUrl = String.format(
                    "http://localhost:8080/Projet_WebServices_war_exploded/api/ecrire?userId1=%s&userId2=%s",
                    login,
                    interlocuteurId
            );

            List<EcrireClient> echanges = fetchList(apiUrl, jwt, new TypeReference<List<EcrireClient>>() {});

            req.setAttribute("echanges", echanges);
            req.setAttribute("interlocuteurId", interlocuteurId);

            req.getRequestDispatcher("conversation.jsp").forward(req, res);

        } catch (IOException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    private <T> List<T> fetchList(String apiUrl, String jwt, TypeReference<List<T>> typeRef) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + jwt);
        conn.setRequestProperty("Accept", "application/json");

        try (InputStream is = conn.getInputStream()) {
            return mapper.readValue(is, typeRef);
        } finally {
            conn.disconnect();
        }
    }
}
