package Controller;

import DAO.EcrireDAO;
import POJO.Ecrire;
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

@WebServlet(name = "EcrireController", urlPatterns = {"/ecrires"})
public class EcrireController extends HttpServlet {

    private final EcrireDAO ecrireDAO = new EcrireDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    // Récupère la clé EcrireId depuis un seul paramètre id_message
    private EcrireId extractEcrireId(HttpServletRequest req) throws NumberFormatException {
        String idMessageStr = req.getParameter("id_message");
        if (idMessageStr == null) {
            return null;
        }
        int idMessage = Integer.parseInt(idMessageStr);
        return new EcrireId(idMessage);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (req.getParameter("id_message") == null) {
            // Retourne la liste complète
            List<Ecrire> ecrires = ecrireDAO.findAll();
            mapper.writeValue(resp.getWriter(), ecrires);
            return;
        }

        try {
            EcrireId id = extractEcrireId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }
            Ecrire ecrire = ecrireDAO.findById(id.getMessage());
            if (ecrire != null) {
                mapper.writeValue(resp.getWriter(), ecrire);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Ecrire not found\"}");
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
            Ecrire newEcrire = mapper.readValue(reader, Ecrire.class);
            ecrireDAO.create(newEcrire);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), newEcrire);
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
            EcrireId id = extractEcrireId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }

            Ecrire existing = ecrireDAO.findById(id.getMessage());
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Ecrire not found\"}");
                return;
            }

            try (BufferedReader reader = req.getReader()) {
                Ecrire updatedEcrire = mapper.readValue(reader, Ecrire.class);

                // S'assurer que la clé reste cohérente
                updatedEcrire.setMessage(existing.getMessage());

                ecrireDAO.update(updatedEcrire);
                mapper.writeValue(resp.getWriter(), updatedEcrire);
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
            EcrireId id = extractEcrireId(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing id_message parameter\"}");
                return;
            }

            Ecrire ecrire = ecrireDAO.findById(id.getMessage());
            if (ecrire == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Ecrire not found\"}");
                return;
            }

            ecrireDAO.delete(ecrire);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id_message parameter\"}");
        }
    }
}
