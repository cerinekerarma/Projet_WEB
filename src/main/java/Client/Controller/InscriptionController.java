package Client.Controller;

import Client.POJO.UserClient;
import DAO.UserDAO;
import POJO.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@WebServlet("/inscription")
public class InscriptionController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO(); // utilise ton constructeur avec persistence.xml
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les données du formulaire
        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Vérification basique
        if (login == null || email == null || password == null ||
                login.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs sont obligatoires.");
            request.getRequestDispatcher("/inscription.jsp").forward(request, response);
            return;
        }

        // Vérifie si un utilisateur avec ce login existe déjà
        if (userDAO.findById(login) != null) {
            request.setAttribute("erreur", "Ce login existe déjà.");
            request.getRequestDispatcher("/inscription.jsp").forward(request, response);
            return;
        }

        // Hachage du mot de passe avec SHA-256
        byte[] hashedPassword;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hashedPassword = digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Erreur lors du hachage du mot de passe", e);
        }

        // Création de l'utilisateur
        User user = new User();
        user.setId(login);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setDateCreation(new Date());

        try {
            userDAO.create(user);
            response.sendRedirect("login.jsp");
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur lors de la création de l'utilisateur : " + e.getMessage());
            request.getRequestDispatcher("/inscription.jsp").forward(request, response);
        }
    }
}
