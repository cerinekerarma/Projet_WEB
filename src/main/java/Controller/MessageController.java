package Controller;

import POJO.Message;
import POJO.User;
import DAO.MessageDAO;
import DAO.UserDAO; // pour récupérer l'auteur lors de la création ou update
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "MessageController", urlPatterns = {"/api/messages"})
public class MessageController extends HttpServlet {

    private MessageDAO messageDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        messageDAO = new MessageDAO();
        objectMapper = new ObjectMapper();
        // Config pour Date en ISO8601 si besoin
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // GET /api/messages         -> liste tous les messages
    // GET /api/messages?id=1    -> message id=1
    // GET /api/messages?serverId=2  -> messages du canal 2
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String idParam = req.getParameter("id");
        String serverIdParam = req.getParameter("serverId");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Message message = messageDAO.findById(id);
                if (message == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(message));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (serverIdParam != null) {
                int serverId = Integer.parseInt(serverIdParam);
                List<Message> messages = messageDAO.findByServerId(serverId);
                resp.getWriter().write(objectMapper.writeValueAsString(messages));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                List<Message> messages = messageDAO.findAll();
                resp.getWriter().write(objectMapper.writeValueAsString(messages));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID parameter");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    // POST /api/messages
    // Corps JSON (message privé) : { "contenu": "...", "sendDate": "...", "sender": { "id": ... }, "receiver": { "id": ... } }
    // Corps JSON (message serveur) : { "contenu": "...", "sendDate": "...", "auteur": { "id": ... }, "serverId": ... }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var jsonNode = objectMapper.readTree(req.getInputStream());

            Message message = new Message();
            message.setContenu(jsonNode.get("contenu").asText());

            // Parsing de la date si présente, sinon date actuelle
            if (jsonNode.has("sendDate")) {
                String dateStr = jsonNode.get("sendDate").asText();
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                message.setSendDate(date);
            } else {
                message.setSendDate(new Date());
            }

            messageDAO.create(message);

            if (jsonNode.has("serverId") && jsonNode.has("auteur")) {
                Integer serverId = jsonNode.get("serverId").asInt();
                Integer auteurId = jsonNode.get("auteur").get("id").asInt();
                messageDAO.insertIntoPublier(message.getId(), serverId, auteurId);
            } else if (jsonNode.has("sender") && jsonNode.has("receiver")) {
                Integer senderId = jsonNode.get("sender").get("id").asInt();
                Integer receiverId = jsonNode.get("receiver").get("id").asInt();
                messageDAO.insertIntoEcrire(message.getId(), senderId, receiverId);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Champs JSON insuffisants pour déterminer le type de message.");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Message créé avec succès.");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erreur lors de la création du message : " + e.getMessage());
        }
    }

    // PUT /api/messages?id=1
    // Corps JSON avec champs à modifier (ex: contenu)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Message existingMessage = messageDAO.findById(id);
            if (existingMessage == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                return;
            }

            Message updatedMessage = objectMapper.readValue(req.getInputStream(), Message.class);

            // Mise à jour des champs (exemple)
            if (updatedMessage.getContenu() != null) {
                existingMessage.setContenu(updatedMessage.getContenu());
            }
            if (updatedMessage.getSendDate() != null) {
                existingMessage.setSendDate(updatedMessage.getSendDate());
            }

            messageDAO.update(existingMessage);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingMessage));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message data: " + e.getMessage());
        }
    }

    // DELETE /api/messages?id=1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Message message = messageDAO.findById(id);
            if (message == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                return;
            }
            messageDAO.delete(message);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message ID");
        }
    }
}
