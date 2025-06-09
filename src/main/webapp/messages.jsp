<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Client Messagerie</title>
    <script>
        function loadMessagesChannel() {
            let channelId = document.getElementById("channelSelect").value;
            fetch(`/api/messages?channelId=${channelId}`)
                .then(response => response.json())
                .then(data => {
                    let messagesDiv = document.getElementById("messages");
                    messagesDiv.innerHTML = "";
                    data.forEach(msg => {
                        messagesDiv.innerHTML += `
                    <div>
                        <strong>${msg.auteur.username}</strong>: ${msg.contenu}
                        <em>${new Date(msg.sendDate).toLocaleString()}</em>
                        <button onclick="reactMessage(${msg.id}, 'like')">👍</button>
                    </div>`;
                    });
                });
        }


        function sendMessage() {
            let channelId = document.getElementById("channelSelect").value;
            let content = document.getElementById("messageInput").value;
            let senderId = document.getElementById("senderId").value;

            let message = {
                content: content,
                sender: { id: parseInt(senderId) },
                channel: { id: parseInt(channelId) }
            };

            fetch('/api/message', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(message)
            })
                .then(response => {
                    if (response.ok) {
                        loadMessagesChannel();
                        document.getElementById("messageInput").value = "";
                    } else {
                        alert("Erreur lors de l'envoi du message");
                    }
                });
        }
    </script>
</head>
<body>
<h1>Messagerie</h1>

<label for="channelSelect">Choisir un canal :</label>
<select id="channelSelect" onchange="loadMessagesChannel()">
    <option value="1">Canal 1</option>
    <option value="2">Canal 2</option>
    <!-- Remplir dynamiquement avec JSP ou via fetch -->
</select>

<div id="messages" style="border:1px solid #ccc; height:300px; overflow:auto; margin-top:10px; padding:5px;">
    <!-- Messages chargés ici -->
</div>

<br/>

<input type="hidden" id="senderId" value="1" /> <!-- Id de l'utilisateur connecté -->

<input type="text" id="messageInput" placeholder="Votre message" style="width:80%;" />
<button onclick="sendMessage()">Envoyer</button>

<button onclick="reactMessage(messageId, 'like')">👍</button>

<script>
    function reactMessage(messageId, reaction) {
        fetch('/api/message/react', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ messageId: messageId, reaction: reaction })
        }).then(response => {
            if (response.ok) {
                alert('Réaction enregistrée');
            } else {
                alert('Erreur');
            }
        });
    }
</script>

</body>
</html>
