<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <title>Connexion - Mon App</title>
    <style>
        /* Style inspiré de Discord */
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
        input[type="text"]::placeholder,
        input[type="password"]::placeholder {
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
    </style>
</head>
<body>
<div class="login-container">
    <h2>Connexion</h2>

    <%
        String error = request.getParameter("error");
        if (error != null) {
    %>
    <div class="error"><%= error %></div>
    <%
        }
    %>

    <form action="AuthClientController" method="post">
        <label for="login">Login</label>
        <input type="text" id="login" name="login" placeholder="user123" required>

        <label for="password">Mot de passe</label>
        <input type="password" id="password" name="password" placeholder="Votre mot de passe" required>

        <button type="submit">Se connecter</button>
    </form>

    <div class="footer">
        Pas encore de compte ? <a href="inscription.jsp">Inscrivez-vous</a>
    </div>
</div>
</body>
</html>
