package Client.Controller;

import Client.POJO.ServerClient;
import Client.POJO.UserClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/HomeController")
public class HomeController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("jwt") == null) {
            res.sendRedirect("login.jsp?error=Veuillez vous reconnecter");
            return;
        }
        String login = (String) session.getAttribute("login");
        String jwt = (String) session.getAttribute("jwt");

        try {
            // Appel aux serveurs
            List<ServerClient> serveurs = fetchList("http://localhost:8080/Projet_WebServices_war_exploded/api/servers?adminId=" + login , jwt, new TypeReference<>() {});
            req.setAttribute("serveurs", serveurs);

            // Appel aux utilisateurs pour les messages privés
            //List<UserClient> conversations = fetchList("http://localhost:8080/Projet_WebServices_war_exploded/api/conversations", jwt, new TypeReference<>() {});
            List<UserClient> conversations = new ArrayList<UserClient>();
            req.setAttribute("conversationsPrivees", conversations);

        } catch (IOException e) {
            res.sendError(500, "Erreur lors du chargement des données : " + e.getMessage());
            return;
        }

        req.getRequestDispatcher("accueil.jsp").forward(req, res);
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
