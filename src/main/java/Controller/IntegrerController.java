package Controller;

import DAO.IntegrerDAO;
import POJO.Integrer;
import POJO.IntegrerId;
import POJO.User;
import POJO.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IntegrerController", urlPatterns = {"/integrer"})
public class IntegrerController extends HttpServlet {

    private final IntegrerDAO integrerDAO = new IntegrerDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Récupère l'ID composite depuis les paramètres de la requête.
     */
    private IntegrerId extractId(HttpServletRequest req) throws NumberFormatException {
        String userIdStr = req.getParameter("user");
        String serverIdStr = req.getParameter("server");

        if (userIdStr == null || serverIdStr == null) {
            return null;
        }

        Integer userId = Integer.parseInt(userIdStr);
        Integer serverId = Integer.parseInt(serverIdStr);

        return new IntegrerId(userId, serverId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        IntegrerId id = extractId(req);
        if (id == null) {
            // Pas d'ID donné => retourner tous les Integrer
            List<Integrer> integrers = integrerDAO.findAll();
            mapper.writeValue(resp.getWriter(), integrers);
            return;
        }

        Integrer integrer = integrerDAO.findById(id.getServer());
        if (integrer != null) {
            mapper.writeValue(resp.getWriter(), integrer);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Integrer not found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = req.getReader()) {
            Integrer newIntegrer = mapper.readValue(reader, Integrer.class);
            integrerDAO.create(newIntegrer);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), newIntegrer);
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        IntegrerId id;
        try {
            id = extractId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing user or server parameter\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid user or server parameter\"}");
            return;
        }

        Integrer existing = integrerDAO.findById(id.getServer());
        if (existing == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Integrer not found\"}");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            Integrer updatedIntegrer = mapper.readValue(reader, Integrer.class);
            // Assure-toi que les IDs restent cohérents
            updatedIntegrer.setUser(existing.getUser());
            updatedIntegrer.setServer(existing.getServer());

            integrerDAO.update(updatedIntegrer);
            mapper.writeValue(resp.getWriter(), updatedIntegrer);
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        IntegrerId id;
        try {
            id = extractId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing user or server parameter\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid user or server parameter\"}");
            return;
        }

        Integrer integrer = integrerDAO.findById(id.getServer());
        if (integrer == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Integrer not found\"}");
            return;
        }

        integrerDAO.delete(integrer);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
