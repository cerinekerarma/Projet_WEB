package Controller;

import POJO.User;
import DAO.UserDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet(name = "UserController", urlPatterns = {"/api/users"})
public class UserController extends HttpServlet {

    private UserDAO userDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }

    // GET /api/users       -> liste tous les users
    // GET /api/users?id=1  -> user id=1
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String idParam = req.getParameter("id");

        if (idParam == null) {
            // Retourne la liste de tous les users
            List<User> users = userDAO.findAll();
            resp.getWriter().write(objectMapper.writeValueAsString(users));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            try {
                int id = Integer.parseInt(idParam);
                User user = userDAO.findById(id);
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                resp.getWriter().write(objectMapper.writeValueAsString(user));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            }
        }
    }

    // POST /api/users
    // Corps JSON: { "email": "...", "password": "...", "creationDate": "yyyy-MM-dd" }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = objectMapper.readValue(req.getInputStream(), User.class);
            // Enregistrement date creation si null
            if (user.getCreationDate() == null) {
                user.setDateCreation(new Date());
            }
            userDAO.create(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user data: " + e.getMessage());
        }
    }

    // PUT /api/users?id=1
    // Corps JSON avec champs à modifier (ex: email, password)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            User existingUser = userDAO.findById(id);
            if (existingUser == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            User updatedUser = objectMapper.readValue(req.getInputStream(), User.class);

            // Mise à jour des champs (exemple simple, tu peux améliorer)
            if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword());
            // on ne met pas à jour la creationDate

            userDAO.update(existingUser);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(existingUser));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user data: " + e.getMessage());
        }
    }

    // DELETE /api/users?id=1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            User user = userDAO.findById(id);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            userDAO.delete(user);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
}
