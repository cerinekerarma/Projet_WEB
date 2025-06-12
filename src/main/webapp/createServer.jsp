<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Créer un serveur</title>
  <style>
    body {
      background-color: #36393f;
      color: white;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      padding: 30px;
    }
    h2 {
      text-align: center;
      margin-bottom: 30px;
      color: #dcddde;
    }
    form {
      max-width: 400px;
      margin: 0 auto;
      background-color: #2f3136;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 0 10px #00000080;
    }
    label {
      display: block;
      margin-bottom: 8px;
      font-weight: bold;
      color: #b9bbbe;
    }
    input[type="text"] {
      width: 100%;
      padding: 10px;
      border-radius: 5px;
      border: none;
      background-color: #202225;
      color: white;
      font-size: 16px;
      margin-bottom: 20px;
      outline: none;
    }
    input[type="submit"] {
      background-color: #5865f2;
      border: none;
      padding: 10px 20px;
      border-radius: 25px;
      color: white;
      font-weight: bold;
      cursor: pointer;
      transition: background-color 0.3s ease;
      width: 100%;
      font-size: 16px;
    }
    input[type="submit"]:hover {
      background-color: #4752c4;
    }
    a.back-arrow {
      color: white;
      text-decoration: none;
      font-size: 24px;
      font-weight: bold;
      display: inline-block;
      margin-bottom: 30px;
    }
    a.back-arrow:hover {
      color: #5865f2;
    }
  </style>
</head>
<body>

<a href="HomeController" class="back-arrow">&#8592; Retour</a>

<h2>Créer un nouveau serveur</h2>

<% if (request.getAttribute("errorMessage") != null) { %>
<div style="color: #f04747; text-align:center; margin-bottom: 20px;">
  <%= request.getAttribute("errorMessage") %>
</div>
<% } %>

<form action="CreateServerController" method="post">
  <label for="serverName">Nom du serveur :</label>
  <input type="text" id="serverName" name="serverName" required placeholder="Nom du serveur" />

  <input type="submit" value="Créer le serveur" />
</form>

</body>
</html>
