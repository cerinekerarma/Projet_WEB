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
    // Corps JSON: { "contenu": "...", "sendDate": "...", "auteur": { "id": ... } }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Message message = objectMapper.readValue(req.getInputStream(), Message.class);

            // Si pas de date, on met la date actuelle
            if (message.getSendDate() == null) {
                message.setSendDate(new Date());
            }

            messageDAO.create(message);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message data: " + e.getMessage());
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
