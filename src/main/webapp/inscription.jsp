<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Inscription - Mon App</title>
    <style>
        body {
            background-color: #36393f;
            color: #fff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .login-container {
            background-color: #2f3136;
            padding: 40px;
            border-radius: 8px;
            width: 320px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.7);
        }
        h2 {
            margin-bottom: 20px;
            text-align: center;
            font-weight: 700;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-size: 14px;
            color: #b9bbbe;
        }
        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border: none;
            border-radius: 5px;
            background-color: #202225;
            color: #dcddde;
            font-size: 16px;
        }
        input::placeholder {
            color: #72767d;
        }
        button {
            width: 100%;
            padding: 12px;
            background-color: #5865f2;
            border: none;
            border-radius: 5px;
            color: white;
            font-weight: 700;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #4752c4;
        }
        .error {
            background-color: #f04747;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
            font-weight: 600;
            color: white;
        }
        .success {
            background-color: #43b581;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
            font-weight: 600;
            color: white;
        }
        .footer {
            text-align: center;
            margin-top: 10px;
            color: #72767d;
            font-size: 14px;
        }
        a {
            color: #5865f2;
            text-decoration: none;
        }

        .logout-btn {
            display: block;
            background-color: #f04747;
            color: white;
            padding: 10px 15px;
            text-align: center;
            border-radius: 5px;
            text-decoration: none;
            margin-bottom: 10px;
            font-weight: bold;
        }

        .add-friend-btn {
            display: block;
            background-color: #43b581;
            color: white;
            padding: 10px 15px;
            text-align: center;
            border-radius: 5px;
            text-decoration: none;
            font-weight: bold;
        }

    </style>
</head>
<body>
<div class="login-container">
    <h2>Inscription</h2>

    <%
        String erreur = (String) request.getAttribute("erreur");
        String succes = (String) request.getAttribute("succes");
        if (erreur != null) {
    %>
    <div class="error"><%= erreur %></div>
    <%
        }
        if (succes != null) {
    %>
    <div class="success"><%= succes %></div>
    <%
        }
    %>

    <form action="inscription" method="post">
        <label for="login">Login</label>
        <input type="text" id="login" name="login" placeholder="user123" required>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="votre@email.com" required>

        <label for="password">Mot de passe</label>
        <input type="password" id="password" name="password" placeholder="Votre mot de passe" required>

        <button type="submit">S'inscrire</button>
    </form>

    <div class="footer">
        Déjà un compte ? <a href="login.jsp">Connectez-vous</a>
    </div>
</div>
</body>
</html>
