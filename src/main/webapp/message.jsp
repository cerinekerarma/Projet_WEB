<%@ page import="java.util.List" %>
<%@ page import="Client.POJO.MessageClient" %>

<h2>Messages</h2>

<%
    List<MessageClient> messages = (List<MessageClient>) request.getAttribute("messages");
    if (messages != null) {
        for (MessageClient msg : messages) {
%>
<div>
    <strong>Inconnu</strong> : <%= msg.getContenu() %> <em><%= msg.getSendDate() %></em>
</div>
<%
    }
} else {
%>
<p>Aucun message trouvé.</p>
<%
    }
%>
