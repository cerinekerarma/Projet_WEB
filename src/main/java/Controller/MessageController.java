package Controller;

import DAO.MessageDAO;
import POJO.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MessageController", urlPatterns = {"/messages/*"})
public class MessageController extends HttpServlet {

    private MessageDAO messageDAO = new MessageDAO();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // / or /{id}

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            // Récupérer tous les messages
            List<Message> messages = messageDAO.findAll();
            objectMapper.writeValue(resp.getWriter(), messages);
        } else {
            // Récupérer message par id
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                Message message = messageDAO.findById(id);
                if (message != null) {
                    objectMapper.writeValue(resp.getWriter(), message);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Message not found\"}");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid message ID\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Message newMessage = objectMapper.readValue(req.getReader(), Message.class);
            messageDAO.create(newMessage);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), newMessage);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Message ID missing\"}");
            return;
        }

        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            Message existing = messageDAO.findById(id);
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Message not found\"}");
                return;
            }
            Message updatedMessage = objectMapper.readValue(req.getReader(), Message.class);
            updatedMessage.setId(id);  // s'assurer que l'id correspond
            messageDAO.update(updatedMessage);
            objectMapper.writeValue(resp.getWriter(), updatedMessage);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid message ID\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Message ID missing\"}");
            return;
        }

        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            Message message = messageDAO.findById(id);
            if (message == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Message not found\"}");
                return;
            }
            messageDAO.delete(message);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid message ID\"}");
        }
    }
}
