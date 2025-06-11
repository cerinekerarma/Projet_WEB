package Controller;

import POJO.Emettre;
import POJO.Message;
import POJO.User;
import DAO.EmettreDAO;
import DAO.MessageDAO;
import DAO.UserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "EmettreController", urlPatterns = {"/api/emettre"})
public class EmettreController extends HttpServlet {

    private EmettreDAO emettreDAO;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        emettreDAO = new EmettreDAO();
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        objectMapper = new ObjectMapper();
    }

    // GET /api/emettre
    // GET /api/emettre?userId=1&messageId=2
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String userIdParam = req.getParameter("userId");
        String messageIdParam = req.getParameter("messageId");

        if (userIdParam == null && messageIdParam == null) {
            // Liste tous les Emettre
            List<Emettre> allEmettre = emettreDAO.findAll();
            resp.getWriter().write(objectMapper.writeValueAsString(allEmettre));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (userIdParam != null && messageIdParam != null) {
            try {
                int userId = Integer.parseInt(userIdParam);
                int messageId = Integer.parseInt(messageIdParam);

                User user = userDAO.findById(userId);
                Message message = messageDAO.findById(messageId);

                if (user == null || message == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User or Message not found");
                    return;
                }

                Emettre emettre = emettreDAO.findById(messageId, userId);
                if (emettre == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Emettre not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(emettre));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or messageId");
            }
        } else if (userIdParam != null) {
            try {
                int userId = Integer.parseInt(userIdParam);
                List<Emettre> list = emettreDAO.findByUserId(userId);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId");
            }
        } else {
            try {
                int messageId = Integer.parseInt(messageIdParam);
                List<Emettre> list = emettreDAO.findByMessageId(messageId);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid messageId");
            }
        }
    }

    // POST /api/emettre
    // Corps JSON : { "user": { "id": 1 }, "message": { "id": 2 }, "reaction": "like" }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Emettre emettre = objectMapper.readValue(req.getInputStream(), Emettre.class);

            if (emettre.getUser() == null || emettre.getMessage() == null || emettre.getReaction() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User, Message and Reaction must be provided");
                return;
            }

            User user = userDAO.findById(emettre.getUser().getId());
            Message message = messageDAO.findById(emettre.getMessage().getId());

            if (user == null || message == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User or Message not found");
                return;
            }

            emettre.setUser(user);
            emettre.setMessage(message);

            emettreDAO.create(emettre);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(emettre));

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Emettre data: " + e.getMessage());
        }
    }

    // PUT /api/emettre?userId=1&messageId=2
    // Corps JSON : { "reaction": "love", "reactionDate": "2024-06-09T12:00:00" }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        String messageIdParam = req.getParameter("messageId");

        if (userIdParam == null || messageIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId and messageId are required");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            int messageId = Integer.parseInt(messageIdParam);

            Emettre existingEmettre = emettreDAO.findById(messageId, userId);
            if (existingEmettre == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Emettre not found");
                return;
            }

            Emettre updatedEmettre = objectMapper.readValue(req.getInputStream(), Emettre.class);

            // Met à jour les champs modifiables
            if (updatedEmettre.getReaction() != null) {
                existingEmettre.setReaction(updatedEmettre.getReaction());
            }

            emettreDAO.update(existingEmettre);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingEmettre));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or messageId");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Emettre data: " + e.getMessage());
        }
    }

    // DELETE /api/emettre?userId=1&messageId=2
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        String messageIdParam = req.getParameter("messageId");

        if (userIdParam == null || messageIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId and messageId are required");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            int messageId = Integer.parseInt(messageIdParam);

            Emettre emettre = emettreDAO.findById(messageId, userId);
            if (emettre == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Emettre not found");
                return;
            }

            emettreDAO.delete(emettre);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or messageId");
        }
    }
}
