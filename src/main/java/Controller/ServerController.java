package Controller;

import POJO.Server;
import POJO.User;
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

@WebServlet(name = "ServerController", urlPatterns = {"/api/servers"})
public class ServerController extends HttpServlet {

    private ServerDAO serverDAO;
    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        serverDAO = new ServerDAO();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    // GET /api/servers           -> liste tous les serveurs
    // GET /api/servers?id=1      -> serveur id=1
    // GET /api/servers?adminId=2 -> serveurs gérés par admin id=2
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");
        String adminIdParam = req.getParameter("adminId");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Server server = serverDAO.findById(id);
                if (server == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Server not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(server));
            } else if (adminIdParam != null) {
                int adminId = Integer.parseInt(adminIdParam);
                List<Server> servers = serverDAO.findByAdminId(adminId);
                resp.getWriter().write(objectMapper.writeValueAsString(servers));
            } else {
                List<Server> servers = serverDAO.findAll();
                resp.getWriter().write(objectMapper.writeValueAsString(servers));
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter");
        }
    }

    // POST /api/servers
    // JSON attendu : { "nom": "...", "admin": { "id": 1 } }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Server server = objectMapper.readValue(req.getInputStream(), Server.class);

            // Vérifier que l'admin existe
            if (server.getAdmin() == null || server.getAdmin().getId() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Admin ID is required");
                return;
            }
            User admin = userDAO.findById(server.getAdmin().getId());
            if (admin == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Admin user not found");
                return;
            }

            server.setAdmin(admin);
            serverDAO.create(server);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(server));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid server data: " + e.getMessage());
        }
    }

    // PUT /api/servers?id=1
    // JSON avec champs à modifier, ex: { "nom": "Nouveau nom", "admin": {"id": 2} }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Server existingServer = serverDAO.findById(id);
            if (existingServer == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Server not found");
                return;
            }

            Server updatedServer = objectMapper.readValue(req.getInputStream(), Server.class);

            if (updatedServer.getNom() != null) {
                existingServer.setNom(updatedServer.getNom());
            }

            if (updatedServer.getAdmin() != null && updatedServer.getAdmin().getId() != null) {
                User newAdmin = userDAO.findById(updatedServer.getAdmin().getId());
                if (newAdmin == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "New admin user not found");
                    return;
                }
                existingServer.setAdmin(newAdmin);
            }

            serverDAO.update(existingServer);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingServer));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid server ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid server data: " + e.getMessage());
        }
    }

    // DELETE /api/servers?id=1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Server server = serverDAO.findById(id);
            if (server == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Server not found");
                return;
            }
            serverDAO.delete(server);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid server ID");
        }
    }
}
