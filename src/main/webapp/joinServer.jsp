<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Rejoindre un Serveur</title>
  <style>
    body {
      background-color: #36393f;
      color: white;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      padding: 30px;
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
    form {
      max-width: 400px;
    }
    input[type="text"] {
      width: 100%;
      padding: 5px;
      margin-top: 5px;
      margin-bottom: 15px;
      border-radius: 4px;
      border: none;
      font-size: 16px;
    }
    input[type="submit"] {
      background-color: #5865f2;
      border: none;
      padding: 8px 15px;
      border-radius: 4px;
      color: white;
      font-weight: bold;
      cursor: pointer;
      transition: background-color 0.3s ease;
    }
    input[type="submit"]:hover {
      background-color: #4752c4;
    }
    .error {
      background-color: #f04747;
      padding: 10px;
      border-radius: 6px;
      margin-bottom: 15px;
      font-weight: bold;
      max-width: 400px;
    }
  </style>
</head>
<body>

<a href="HomeController" class="back-arrow">&#8592; Retour</a>

<h2>Rejoindre un serveur</h2>

<% String errorMessage = (String) request.getAttribute("errorMessage");
  if (errorMessage != null) { %>
<div class="error"><%= errorMessage %></div>
<% } %>

<form action="JoinServerController" method="post">
  <label for="serverId">Code du serveur :</label><br>
  <input type="text" name="serverId" id="serverId" required style="padding:5px;">
  <input type="submit" value="Rejoindre" style="padding:5px 10px; margin-top:10px;">
</form>

</body>
</html>
