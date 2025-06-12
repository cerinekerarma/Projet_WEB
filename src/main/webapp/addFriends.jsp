<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Client.POJO.EcrireClient" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ajouter un ami</title>
    <style>
        body {
            background-color: #36393f;
            color: #dcddde;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .back-arrow {
            color: white;
            text-decoration: none;
            font-size: 24px;
            margin-bottom: 15px;
            display: inline-block;
            font-weight: bold;
        }

        .back-arrow:hover {
            color: #5865f2;
        }

        .container {
            background-color: #2f3136;
            padding: 30px;
            border-radius: 8px;
            max-width: 500px;
            margin: 0 auto;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        }

        h2 {
            color: #ffffff;
            text-align: center;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        input[type="text"] {
            width: 100%;
            padding: 12px;
            background-color: #40444b;
            border: 1px solid #202225;
            border-radius: 5px;
            color: #dcddde;
            font-size: 16px;
            box-sizing: border-box;
        }

        input[type="text"]:focus {
            outline: none;
            border-color: #5865f2;
        }

        button {
            width: 100%;
            padding: 12px;
            background-color: #5865f2;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        button:hover {
            background-color: #4752c4;
        }

        .message {
            padding: 12px;
            margin: 15px 0;
            border-radius: 5px;
            text-align: center;
            font-weight: 600;
        }

        .success {
            background-color: #43b581;
            color: white;
        }

        .error {
            background-color: #f04747;
            color: white;
        }

        .conversation-link {
            display: inline-block;
            margin-top: 15px;
            padding: 8px 15px;
            background-color: #43b581;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 600;
            transition: background-color 0.2s;
        }

        .conversation-link:hover {
            background-color: #3aa374;
        }
    </style>
</head>
<body>

<a href="HomeController" class="back-arrow">&#8592; Retour</a>

<div class="container">
    <h2>Ajouter un ami</h2>

    <%-- Affichage des messages d'erreur/succès --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="message error">
        <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
    <div class="message success">
        <%= request.getAttribute("success") %>
        <a href="ConversationController?userId=<%= request.getAttribute("friendId") %>"
           class="conversation-link">
            Voir la conversation
        </a>
    </div>
    <% } %>

    <form id="addFriendForm" method="post" action="AddFriendController">
        <div class="form-group">
            <input type="text" name="friendUsername"
                   placeholder="Entrez le nom d'utilisateur" required>
        </div>
        <button type="submit">Rechercher et ajouter</button>
    </form>
</div>

</body>
</html>