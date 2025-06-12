<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 2em;
            background-color: #f0f2f5;
        }
        .login-container {
            background-color: white;
            padding: 2em;
            max-width: 400px;
            margin: auto;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
        }
        input[type=text], input[type=password] {
            width: 100%;
            padding: 0.8em;
            margin: 0.5em 0 1em 0;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 0.8em;
            background-color: #007bff;
            border: none;
            color: white;
            font-weight: bold;
            border-radius: 4px;
            cursor: pointer;
        }
        .error {
            color: red;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Connexion</h2>

    <form action="AuthClientController" method="post">
        <label for="login">Login :</label>
        <input type="text" id="login" name="login" required>

        <label for="password">Mot de passe :</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Se connecter</button>
    </form>

    <%
        String error = request.getParameter("error");
        if (error != null) {
    %>
    <p class="error"><%= error %></p>
    <%
        }
    %>
</div>
</body>
</html>
