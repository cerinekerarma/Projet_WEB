<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List, POJO.Message, POJO.User" %>
<%@ page import="DAO.MessageDAO" %>
<%
    // Récupération des paramètres
    Integer channelId = null;
    try {
        channelId = Integer.parseInt(request.getParameter("channelId"));
    } catch (Exception e) {}

    if (channelId == null) {
        channelId = 1; // canal par défaut
    }

    // Récupérer la liste des messages depuis ta DAO (à adapter)
    // Ici on suppose que tu as une classe MessageDAO avec méthode findByChannelId
    MessageDAO messageDAO = new MessageDAO();
    List<Message> messages = messageDAO.findByServerId(channelId);

    // Récupérer l'utilisateur connecté (simulé ici, à remplacer par la session réelle)
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Canal #<%= channelId %> - Messagerie</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        .message { border-bottom: 1px solid #ddd; padding: 10px 0; }
        .author { font-weight: bold; }
        .text { margin: 5px 0; }
        .like-button { cursor: pointer; color: #0066cc; background: none; border: none; }
        form { margin-top: 20px; }
        textarea { width: 100%; height: 70px; }
        .error { color: red; font-weight: bold; }
    </style>
</head>
<body>
<h2>Canal #<%= channelId %></h2>

<% if (error != null) { %>
<p class="error"><%= error %></p>
<% } %>

<div>
    <% for (Message m : messages) { %>
    <div class="message">
        <span class="author"><%= m.getUser().getName() %></span> :
        <span class="text"><%= m.getText() %></span>
        <form method="get" action="messageAction.jsp" style="display:inline;">
            <input type="hidden" name="messageId" value="<%= m.getId() %>"/>
            <input type="hidden" name="action" value="like"/>
            <button class="like-button" type="submit">👍 <%= m.getLikesCount() %></button>
        </form>
    </div>
    <% } %>
</div>

<form method="post" action="sendMessage">
    <input type="hidden" name="channelId" value="<%= channelId %>" />
    <textarea name="messageText" placeholder="Écrire un message..." required></textarea><br />
    <button type="submit">Envoyer</button>
</form>
</body>
</html>
