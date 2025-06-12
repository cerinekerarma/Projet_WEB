<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, DAO.*" %>
<%@ page import="Client.Controller.LogoutController" %>
<%@ page import="Client.POJO.UserClient" %>
<%@ page import="Client.POJO.IntegrerClient" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil - Mon App</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #36393f;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: white;
            display: flex;
            height: 100vh;
        }
        .sidebar {
            width: 70px;
            background-color: #202225;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 10px 0;
            gap: 10px;
            overflow-y: auto;
        }
        .sidebar .server {
            width: 50px;
            height: 50px;
            background-color: #5865f2;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.2s ease;
        }
        .sidebar .server:hover {
            background-color: #4752c4;
        }
        .main {
            flex-grow: 1;
            display: flex;
        }
        .private-messages {
            width: 250px;
            background-color: #2f3136;
            padding: 10px;
            overflow-y: auto;
        }
        .private-messages h3 {
            font-size: 14px;
            margin-bottom: 10px;
            color: #b9bbbe;
        }
        .private-messages .user {
            padding: 8px;
            background-color: #36393f;
            border-radius: 5px;
            margin-bottom: 5px;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .private-messages .user:hover {
            background-color: #40444b;
        }
        .chat-area {
            flex-grow: 1;
            background-color: #36393f;
            padding: 20px;
        }
        .welcome {
            font-size: 20px;
            color: #dcddde;
        }

        a.user {
            display: block;
            text-decoration: none;
            color: inherit;
        }

        .logout-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: #f04747;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.2s ease;
        }
        .logout-btn:hover {
            background-color: #d33a3a;
        }

        .add-friend-btn {
            position: absolute;
            top: 60px; /* en dessous du bouton déconnexion */
            right: 10px;
            background-color: #7289da;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.2s ease;
        }

        .add-friend-btn:hover {
            background-color: #5b6eae;
        }

    </style>
</head>
<body>

<!-- Colonne de gauche : Liste des serveurs -->
<div class="sidebar">
    <% List<IntegrerClient> serveurs = (List<IntegrerClient>) request.getAttribute("serveurs"); %>
    <% if (serveurs != null) {
        for (IntegrerClient s : serveurs) { %>
    <div class="server" title="<%= s.getServer().getNom() %>">
        <%= s.getServer().getNom().charAt(0) %>
    </div>
    <%   }
    } else { %>
    <div style="color: #b9bbbe; font-size: 12px;">Aucun serveur</div>
    <% } %>
</div>

<!-- Partie centrale et droite -->
<div class="main">

    <!-- Colonne droite : Conversations privées -->
    <div class="private-messages">
        <h3>Conversations Privées</h3>
        <% List<UserClient> conversations = (List<UserClient>) request.getAttribute("conversationsPrivees"); %>
        <% if (conversations != null) {
            for (UserClient u : conversations) { %>
        <a href="ConversationController?userId=<%= u.getId() %>" class="user">
            <%= u.getId() %>
        </a>
        <%   }
        } else { %>
        <div style="color: #b9bbbe;">Aucune conversation</div>
        <% } %>
    </div>

    <!-- Zone de chat principale -->
    <div class="chat-area">
        <div class="welcome">
            Bienvenue sur 10-corde<br>
            Sélectionnez un serveur ou un utilisateur pour commencer à discuter.
        </div>
    </div>
    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Déconnexion</a>
    <a href="addFriends.jsp" class="add-friend-btn">Ajouter des amis</a>


</div>
</body>
</html>