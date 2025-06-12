<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Client.POJO.PublierClient" %>
<%@ page import="Client.POJO.ServerClient" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    ServerClient server = (ServerClient) request.getAttribute("server");
    List<PublierClient> messages = (List<PublierClient>) request.getAttribute("publications");
    String currentUserId = session.getAttribute("login") != null ? (String) session.getAttribute("login") : "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Messages du serveur <%= server != null ? server.getNom() : "Inconnu" %></title>
    <style>
        body {
            background-color: #36393f;
            color: white;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0; padding: 20px;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        a.back-arrow {
            color: white;
            text-decoration: none;
            font-size: 24px;
            margin-bottom: 15px;
            display: inline-block;
            font-weight: bold;
        }
        a.back-arrow:hover {
            color: #5865f2;
        }
        .message-container {
            max-width: 600px;
            margin: auto;
            flex-grow: 1;
            overflow-y: auto;
        }
        .message {
            padding: 10px 15px;
            margin-bottom: 10px;
            border-radius: 15px;
            max-width: 70%;
            clear: both;
            word-wrap: break-word;
        }
        .sender {
            background-color: #5865f2;
            float: right;
            text-align: right;
        }
        .receiver {
            background-color: #2f3136;
            float: left;
            text-align: left;
        }
        .timestamp {
            font-size: 10px;
            color: #b9bbbe;
            margin-top: 5px;
        }
        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #dcddde;
        }
        form#messageForm {
            max-width: 600px;
            margin: 20px auto 0;
            display: flex;
            gap: 10px;
        }
        form#messageForm input[type="text"] {
            flex-grow: 1;
            padding: 10px 15px;
            border-radius: 25px;
            border: none;
            font-size: 16px;
            outline: none;
        }
        form#messageForm button {
            background-color: #5865f2;
            border: none;
            padding: 0 20px;
            border-radius: 25px;
            color: white;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        form#messageForm button:hover {
            background-color: #4752c4;
        }
    </style>
</head>
<body>

<a href="HomeController" class="back-arrow">&#8592; Retour</a>

<div class="message-container">
    <h2>
        Messages du serveur
        <%= server != null ? server.getNom() : "Inconnu" %>
        <% if (server != null && server.getAdmin() != null) { %>
        <br><small style="font-size: 14px; color: #aaa;">Admin : <%= server.getAdmin().getId() %></small>
        <% } %>
    </h2>

    <%
        if (messages == null || messages.isEmpty()) {
    %>
    <p>Aucun message n'a encore été publié.</p>
    <%
    } else {
        for (PublierClient pub : messages) {
            String senderId = pub.getUser() != null ? pub.getUser().getId() : "inconnu";
            String msgText = pub.getMessage() != null ? pub.getMessage().getContenu() : "";
            String dateText = "";
            if (pub.getMessage() != null && pub.getMessage().getSendDate() != null) {
                dateText = sdf.format(pub.getMessage().getSendDate());
            }
            boolean isCurrentUserSender = senderId.equals(currentUserId);
    %>
    <div class="message <%= isCurrentUserSender ? "sender" : "receiver" %>">
        <div><%= msgText %></div>
        <div class="timestamp"><%= dateText %></div>
    </div>
    <%
            }
        }
    %>
</div>

<form id="messageForm" method="post" action="SendServerMessageController">
    <input type="hidden" name="auteurId" value="<%= currentUserId %>"/>
    <input type="hidden" name="serverId" value="<%= server != null ? server.getId() : "" %>"/>
    <input type="text" name="contenu" placeholder="Écrire un message..." required autocomplete="off" />
    <button type="submit">Envoyer</button>
</form>

</body>
</html>
