package Client.Controller;

import Client.POJO.MessageClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@WebServlet("/MessageClientControler")
public class MessageClientControler extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        try {
            // Appel à l’API pour récupérer tous les messages
            URL url = new URL("http://localhost:8080/Projet_WebServices_war_exploded/api/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            List<MessageClient> messages;
            try (InputStream is = conn.getInputStream()) {
                messages = mapper.readValue(is, new TypeReference<>() {});
            }

            req.setAttribute("messages", messages);
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Erreur client : " + e.getMessage());
            return;
        }

        req.getRequestDispatcher("message.jsp").forward(req, res);
    }
}
