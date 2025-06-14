package Controller;

import POJO.Ecrire;
import POJO.Message;
import POJO.User;
import DAO.EcrireDAO;
import DAO.UserDAO;
import DAO.MessageDAO;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "EcrireController", urlPatterns = {"/api/ecrire"})
public class EcrireController extends HttpServlet {

    private EcrireDAO ecrireDAO;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        ecrireDAO = new EcrireDAO();
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String messageIdParam = req.getParameter("messageId");
        String senderIdParam = req.getParameter("senderId");
        String receiverIdParam = req.getParameter("receiverId");
        String userIdParam = req.getParameter("userId");

        try {
            if (messageIdParam != null) {
                // Récupérer un Ecrire par message ID
                int messageId = Integer.parseInt(messageIdParam);
                Message message = messageDAO.findById(messageId);
                if (message == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                    return;
                }

                Ecrire ecrire = ecrireDAO.findByMessage(message);
                if (ecrire == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ecrire not found for this message");
                    return;
                }

                resp.getWriter().write(objectMapper.writeValueAsString(ecrire));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else if (senderIdParam != null) {
                // Récupérer tous les messages envoyés par un utilisateur
                User sender = userDAO.findById(senderIdParam);
                if (sender == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sender not found");
                    return;
                }

                List<Ecrire> list = ecrireDAO.findBySender(sender);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else if (receiverIdParam != null) {
                // Récupérer tous les messages reçus par un utilisateur
                User receiver = userDAO.findById(receiverIdParam);
                if (receiver == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Receiver not found");
                    return;
                }

                List<Ecrire> list = ecrireDAO.findByReceiver(receiver);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else if (userIdParam != null) {
                // Récupérer tous les interlocuteurs d'un utilisateur
                User user = userDAO.findById(userIdParam);
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }

                List<User> interlocuteurs = ecrireDAO.findDistinctUsersInConversationWith(user);
                resp.getWriter().write(objectMapper.writeValueAsString(interlocuteurs));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else if (req.getParameter("userId1") != null && req.getParameter("userId2") != null) {
                String userId1 = req.getParameter("userId1");
                String userId2 = req.getParameter("userId2");

                User user1 = userDAO.findById(userId1);
                User user2 = userDAO.findById(userId2);

                if (user1 == null || user2 == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User(s) not found");
                    return;
                }

                // Méthode à créer dans EcrireDAO pour récupérer tous les messages échangés entre user1 et user2
                List<Ecrire> conversation = ecrireDAO.findConversationBetweenUsers(user1, user2);

                resp.getWriter().write(objectMapper.writeValueAsString(conversation));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Récupérer tous les Ecrire
                List<Ecrire> all = ecrireDAO.findAll();
                resp.getWriter().write(objectMapper.writeValueAsString(all));
                resp.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter format");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }


    // POST /api/ecrire
    // JSON: { "message": { "id": 1 }, "sender": { "id": 2 }, "receiver": { "id": 3 } }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Ecrire ecrire = objectMapper.readValue(req.getInputStream(), Ecrire.class);

            if (ecrire.getMessage() == null || ecrire.getSender() == null || ecrire.getReceiver() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message, Sender and Receiver must be provided");
                return;
            }

            Message message = messageDAO.findById(ecrire.getMessage().getId());
            User sender = userDAO.findById(ecrire.getSender().getId());
            User receiver = userDAO.findById(ecrire.getReceiver().getId());

            if (message == null || sender == null || receiver == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message, Sender or Receiver not found");
                return;
            }

            ecrire.setMessage(message);
            ecrire.setSender(sender);
            ecrire.setReceiver(receiver);

            ecrireDAO.create(ecrire);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(ecrire));

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Ecrire data: " + e.getMessage());
        }
    }

    // PUT /api/ecrire?messageId=1
    // JSON: { "sendDate": "2023-04-01T12:00:00" }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String messageIdParam = req.getParameter("messageId");

        if (messageIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "messageId is required");
            return;
        }

        try {
            int messageId = Integer.parseInt(messageIdParam);

            Message message = messageDAO.findById(messageId);
            if (message == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                return;
            }

            Ecrire existing = ecrireDAO.findByMessage(message);
            if (existing == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ecrire not found");
                return;
            }

            // Tu peux aussi autoriser la mise à jour d'autres champs si nécessaire

            ecrireDAO.update(existing);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existing));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid messageId");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Ecrire data: " + e.getMessage());
        }
    }

    // DELETE /api/ecrire?messageId=1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String messageIdParam = req.getParameter("messageId");

        if (messageIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "messageId is required");
            return;
        }

        try {
            int messageId = Integer.parseInt(messageIdParam);

            Message message = messageDAO.findById(messageId);
            if (message == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                return;
            }

            Ecrire ecrire = ecrireDAO.findByMessage(message);
            if (ecrire == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ecrire not found");
                return;
            }

            ecrireDAO.delete(ecrire);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid messageId");
        }
    }
}
