<%@ page import="java.util.Vector" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.util.Date" %>
<%@ page import="uk.ac.uclan.thc.model.Session" %>
<%@ page import="uk.ac.uclan.thc.data.SessionFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 09:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Sessions</title>
    </head>

    <body>

<%@ include file="Authenticate.jsp" %>

<%
    if(userEntity == null)
    {
%>
You are not logged in!
<%
    }
    else if(!userEntity.isAdmin())
    {
%>
You are not admin!
<%
    }
    else
    {
        final String categoryUUID = request.getParameter("categoryUUID");

        final Vector<Session> sessions = SessionFactory.getSessionsByCategoryUUID(categoryUUID);
%>

<h1>Sessions</h1>

<p>Number of sessions : <%=sessions.size()%></p>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>APP ID</th>
        <th>PLAYER NAME</th>
        <th>CURRENT QUESTION UUID</th>
        <th>SCORE</th>
    </tr>
    <%
        for(final Session mySession : sessions)
        {
    %>
    <tr>
        <td><%=mySession.getUUID()%></td>
        <td><%=mySession.getAppID()%></td>
        <td><%=mySession.getPlayerName()%></td>
        <td><%=mySession.getCurrentQuestionUUID()%></td>
        <td><%=mySession.getScore()%></td>
    </tr>
    <%
        }
    %>
</table>

<%
    }
%>

</body>
</html>