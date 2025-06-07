package Controller;

import DAO.EmettreDAO;
import POJO.Emettre;
import POJO.EmettreId;
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

@WebServlet(name = "EmettreController", urlPatterns = {"/emettre"})
public class EmettreController extends HttpServlet {

    private final EmettreDAO emettreDAO = new EmettreDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    private EmettreId extractEmettreId(HttpServletRequest req) throws NumberFormatException {
        String idMessageStr = req.getParameter("id_message");
        if (idMessageStr == null) {
            return null;
        }
        int idMessage = Integer.parseInt(idMessageStr);
        return new EmettreId(idMessage);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (req.getParameter("id_message") == null) {
            List<Emettre> emettrres = emettreDAO.findAll();
            mapper.writeValue(resp.getWriter(), emettrres);
            return;
        }

        try {
            EmettreId id = extractEmettreId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }
            Emettre emettre = emettreDAO.findById(id.getMessage());
            if (emettre != null) {
                mapper.writeValue(resp.getWriter(), emettre);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Emettre not found\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id_message parameter\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = req.getReader()) {
            Emettre newEmettre = mapper.readValue(reader, Emettre.class);
            emettreDAO.create(newEmettre);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), newEmettre);
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            EmettreId id = extractEmettreId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }

            Emettre existing = emettreDAO.findById(id.getMessage());
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Emettre not found\"}");
                return;
            }

            try (BufferedReader reader = req.getReader()) {
                Emettre updatedEmettre = mapper.readValue(reader, Emettre.class);
                updatedEmettre.setMessage(existing.getMessage()); // ou autre champ clé
                emettreDAO.update(updatedEmettre);
                mapper.writeValue(resp.getWriter(), updatedEmettre);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id_message parameter\"}");
        } catch (MismatchedInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Malformed JSON\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            EmettreId id = extractEmettreId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }

            Emettre emettre = emettreDAO.findById(id.getMessage());
            if (emettre == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Emettre not found\"}");
                return;
            }

            emettreDAO.delete(emettre);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id_message parameter\"}");
        }
    }
}
