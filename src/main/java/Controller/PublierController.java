package Controller;

import POJO.Publier;
import POJO.Message;
import POJO.Server;
import POJO.User;

import DAO.PublierDAO;
import DAO.MessageDAO;
import DAO.ServerDAO;
import DAO.UserDAO;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "PublierController", urlPatterns = {"/api/publier"})
public class PublierController extends HttpServlet {

    private PublierDAO publierDAO;
    private MessageDAO messageDAO;
    private ServerDAO serverDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        publierDAO = new PublierDAO();
        messageDAO = new MessageDAO();
        serverDAO = new ServerDAO();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String idMessageParam = req.getParameter("id");
        String serverIdParam = req.getParameter("serverId");

        try {
            if (idMessageParam != null) {
                // GET /api/publier?id=xxx
                int idMessage = Integer.parseInt(idMessageParam);
                Message message = messageDAO.findById(idMessage);
                if (message == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                    return;
                }
                Publier publier = publierDAO.findById(idMessage);
                if (publier == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Publier not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(publier));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else if (serverIdParam != null) {
                // ✅ GET /api/publier?serverId=xxx
                int serverId = Integer.parseInt(serverIdParam);
                List<Publier> publierList = publierDAO.findByServerId(serverId);
                resp.getWriter().write(objectMapper.writeValueAsString(publierList));
                resp.setStatus(HttpServletResponse.SC_OK);

            } else {
                // GET /api/publier → liste tous les publier
                List<Publier> publierList = publierDAO.findAll();
                resp.getWriter().write(objectMapper.writeValueAsString(publierList));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter");
        }
    }



    // POST /api/publier
    // JSON attendu : { "message": {"id": ...}, "server": {"id": ...}, "user": {"id": ...} }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Publier publier = objectMapper.readValue(req.getInputStream(), Publier.class);

            if (publier.getMessage() == null || publier.getMessage().getId() == null
                    || publier.getServer() == null || publier.getServer().getId() == null
                    || publier.getUser() == null || publier.getUser().getId() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message, Server, and User IDs are required");
                return;
            }

            Message message = messageDAO.findById(publier.getMessage().getId());
            Server server = serverDAO.findById(publier.getServer().getId());
            User user = userDAO.findById(publier.getUser().getId());

            if (message == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message not found");
                return;
            }
            if (server == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server not found");
                return;
            }
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
                return;
            }

            publier.setMessage(message);
            publier.setServer(server);
            publier.setUser(user);

            publierDAO.create(publier);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(publier));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Publier data: " + e.getMessage());
        }
    }

    // PUT /api/publier?id=xxx
    // JSON avec champs à modifier (server et/ou user). Message ne peut pas être modifié (clé PK)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required");
            return;
        }

        try {
            int idMessage = Integer.parseInt(idParam);
            Publier existingPublier = publierDAO.findById(idMessage);
            if (existingPublier == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Publier not found");
                return;
            }

            Publier updatedPublier = objectMapper.readValue(req.getInputStream(), Publier.class);

            if (updatedPublier.getServer() != null && updatedPublier.getServer().getId() != null) {
                Server newServer = serverDAO.findById(updatedPublier.getServer().getId());
                if (newServer == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server not found");
                    return;
                }
                existingPublier.setServer(newServer);
            }

            if (updatedPublier.getUser() != null && updatedPublier.getUser().getId() != null) {
                User newUser = userDAO.findById(updatedPublier.getUser().getId());
                if (newUser == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
                    return;
                }
                existingPublier.setUser(newUser);
            }

            // Le message ne peut pas être modifié (clé primaire)
            publierDAO.update(existingPublier);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingPublier));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id parameter");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Publier data: " + e.getMessage());
        }
    }

    // DELETE /api/publier?id=xxx
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required");
            return;
        }

        try {
            int idMessage = Integer.parseInt(idParam);
            Publier publier = publierDAO.findById(idMessage);
            if (publier == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Publier not found");
                return;
            }
            publierDAO.delete(publier);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id parameter");
        }
    }
}
