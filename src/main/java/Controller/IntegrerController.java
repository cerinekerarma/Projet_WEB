package Controller;

import POJO.Integrer;
import POJO.User;
import POJO.Server;
import DAO.IntegrerDAO;
import DAO.UserDAO;
import DAO.ServerDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "IntegrerController", urlPatterns = {"/api/integrer"})
public class IntegrerController extends HttpServlet {

    private IntegrerDAO integrerDAO;
    private UserDAO userDAO;
    private ServerDAO serverDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        integrerDAO = new IntegrerDAO();
        userDAO = new UserDAO();
        serverDAO = new ServerDAO();
        objectMapper = new ObjectMapper();
    }

    // GET /api/integrer              -> liste tous les Integrer
    // GET /api/integrer?userId=1&serverId=2  -> intégration spécifique
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String userIdParam = req.getParameter("userId");
        String serverIdParam = req.getParameter("serverId");

        if (userIdParam == null && serverIdParam == null) {
            // Liste tous
            List<Integrer> allIntegrer = integrerDAO.findAll();
            resp.getWriter().write(objectMapper.writeValueAsString(allIntegrer));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (userIdParam != null && serverIdParam != null) {
            // Trouver un Integrer par clé composite
            try {
                String userId = userIdParam;
                int serverId = Integer.parseInt(serverIdParam);

                // On crée un Integrer temporaire avec User et Server chargés
                User user = userDAO.findById(userId);
                Server server = serverDAO.findById(serverId);

                if (user == null || server == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User or Server not found");
                    return;
                }

                Integrer integrer = integrerDAO.findById(userId, serverId);
                if (integrer == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Integrer not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(integrer));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or serverId");
            }
        } else if (userIdParam != null) {
            // Liste les serveurs rejoints par un user
            try {
                int userId = Integer.parseInt(userIdParam);
                List<Integrer> list = integrerDAO.findByUserId(userId);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId");
            }
        } else {
            // Liste les utilisateurs ayant rejoint un serveur
            try {
                int serverId = Integer.parseInt(serverIdParam);
                List<Integrer> list = integrerDAO.findByServerId(serverId);
                resp.getWriter().write(objectMapper.writeValueAsString(list));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid serverId");
            }
        }
    }

    // POST /api/integrer
    // Corps JSON : { "user": { "id": 1 }, "server": { "id": 2 } }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Integrer integrer = objectMapper.readValue(req.getInputStream(), Integrer.class);

            // Vérifier que user et server existent en base
            if (integrer.getUser() == null || integrer.getServer() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User and Server must be provided");
                return;
            }

            User user = userDAO.findById(integrer.getUser().getId());
            Server server = serverDAO.findById(integrer.getServer().getId());
            if (user == null || server == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User or Server not found");
                return;
            }

            integrerDAO.create(integrer);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(integrer));

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Integrer data: " + e.getMessage());
        }
    }

    // PUT /api/integrer?userId=1&serverId=2
    // Corps JSON : { "dateJoined": "2023-04-01T12:00:00" }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        String serverIdParam = req.getParameter("serverId");

        if (userIdParam == null || serverIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId and serverId are required");
            return;
        }

        try {
            String userId = userIdParam;
            int serverId = Integer.parseInt(serverIdParam);

            Integrer existingIntegrer = integrerDAO.findById(userId, serverId);
            if (existingIntegrer == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Integrer not found");
                return;
            }

            integrerDAO.update(existingIntegrer);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingIntegrer));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or serverId");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Integrer data: " + e.getMessage());
        }
    }

    // DELETE /api/integrer?userId=1&serverId=2
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        String serverIdParam = req.getParameter("serverId");

        if (userIdParam == null || serverIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId and serverId are required");
            return;
        }

        try {
            String userId = userIdParam;
            int serverId = Integer.parseInt(serverIdParam);

            Integrer integrer = integrerDAO.findById(userId, serverId);
            if (integrer == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Integrer not found");
                return;
            }

            integrerDAO.delete(integrer);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId or serverId");
        }
    }
}
