<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Client.POJO.EcrireClient" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    List<EcrireClient> echanges = (List<EcrireClient>) request.getAttribute("echanges");
    String interlocuteurId = (String) request.getAttribute("interlocuteurId");
    String currentUserId = session.getAttribute("login") != null ? (String) session.getAttribute("login") : "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Conversation avec <%= interlocuteurId %></title>
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
    <h2>Conversation avec <%= interlocuteurId %></h2>

    <%
        if (echanges == null || echanges.isEmpty()) {
    %>
    <p>Aucun message dans cette conversation.</p>
    <%
    } else {
        for (EcrireClient e : echanges) {
            String senderId = e.getSender().getId();
            String messageText = e.getMessage() != null ? e.getMessage().getContenu() : "";
            String timestamp = "";
            if (e.getMessage() != null && e.getMessage().getSendDate() != null) {
                timestamp = sdf.format(e.getMessage().getSendDate());
            }
            boolean isCurrentUserSender = senderId.equals(currentUserId);
    %>
    <div class="message <%= isCurrentUserSender ? "sender" : "receiver" %>">
        <div><%= messageText %></div>
        <div class="timestamp"><%= timestamp %></div>
    </div>
    <%
            }
        }
    %>
</div>

<form id="messageForm" method="post" action="SendMessageController">
    <input type="hidden" name="senderId" value="<%= currentUserId %>"/>
    <input type="hidden" name="receiverId" value="<%= interlocuteurId %>"/>
    <input type="text" name="contenu" placeholder="Écrire un message..." required autocomplete="off" />
    <button type="submit">Envoyer</button>
</form>

</body>
</html>
