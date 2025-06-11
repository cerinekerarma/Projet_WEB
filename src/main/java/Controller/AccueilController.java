package Controller;

import DAO.PublierDAO;
import POJO.Server;
import POJO.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class AccueilController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        String vue = "connexion.jsp"; // Vue par défaut

        try {
            switch (action) {
                case null:
                    vue = "connexion.jsp";
                    break;
                case "gerer_comptes":
                    HttpSession session = req.getSession(false);
                    if (session == null || session.getAttribute("user") == null) {
                        res.sendRedirect("connexion.jsp");
                        return;
                    }

                    User currentUser = (User) session.getAttribute("user");

                    List<Server> serveurs = serveurDAO.findByUserId(currentUser.getId());
                    List<User> conversations = userDAO.findPrivateContacts(currentUser.getId());

                    req.setAttribute("serveurs", serveurs);
                    req.setAttribute("conversationsPrivees", conversations);

                default:
                    vue = "connexion.jsp";
                    res.sendError(404, "Action non supportée");
                    return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Erreur serveur : " + e.getMessage());
            return;
        }

        req.getRequestDispatcher(vue).forward(req, res);

    }
}
