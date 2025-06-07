package Controller;

import DAO.PublierDAO;
import POJO.Publier;
import POJO.PublierId;
import POJO.Message;

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

@WebServlet(name = "PublierController", urlPatterns = {"/publier"})
public class PublierController extends HttpServlet {

    private final PublierDAO publierDAO = new PublierDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Extrait l'ID du message depuis les paramètres HTTP.
     */
    private PublierId extractId(HttpServletRequest req) throws NumberFormatException {
        String messageIdStr = req.getParameter("message");
        if (messageIdStr == null) return null;

        Integer messageId = Integer.parseInt(messageIdStr);
        return new PublierId(messageId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PublierId id = extractId(req);
        if (id == null) {
            // Pas d'ID donné, on retourne tout
            List<Publier> publies = publierDAO.findAll();
            mapper.writeValue(resp.getWriter(), publies);
            return;
        }

        Publier publier = publierDAO.findById(id.getMessage());
        if (publier != null) {
            mapper.writeValue(resp.getWriter(), publier);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Publier not found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = req.getReader()) {
            Publier newPublier = mapper.readValue(reader, Publier.class);
            publierDAO.create(newPublier);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), newPublier);
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PublierId id;
        try {
            id = extractId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing message parameter\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid message parameter\"}");
            return;
        }

        Publier existing = publierDAO.findById(id.getMessage());
        if (existing == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Publier not found\"}");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            Publier updatedPublier = mapper.readValue(reader, Publier.class);

            // Assure cohérence de l'ID
            updatedPublier.setMessage(existing.getMessage());

            publierDAO.update(updatedPublier);
            mapper.writeValue(resp.getWriter(), updatedPublier);
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PublierId id;
        try {
            id = extractId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing message parameter\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid message parameter\"}");
            return;
        }

        Publier publier = publierDAO.findById(id.getMessage());
        if (publier == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Publier not found\"}");
            return;
        }

        publierDAO.delete(publier);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
