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

        final Category category = CategoryFactory.getCategory(categoryUUID);
        final Vector<Session> sessions = SessionFactory.getSessionsByCategoryUUID(categoryUUID);
%>

<h1>Sessions</h1>

<p>Number of sessions : <%=sessions.size()%></p>

<p>Category: <b><%=category.getName()%></b></p>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>APP ID</th>
        <th>PLAYER NAME</th>
        <th>CURRENT QUESTION UUID</th>
        <th>SCORE</th>
        <th>FINISH TIME</th>
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
    <%
        final long finishTime = mySession.getFinishTime();
        final long milliseconds = finishTime % 1000;
        final String millisecondsS = milliseconds >= 100 ? Long.toString(milliseconds) : milliseconds >= 10 ? "0" + milliseconds : "00" + milliseconds;
        long seconds = finishTime / 1000L;
        final String secondsS = (seconds % 60L) < 10L ? "0" + (seconds % 60L) : Long.toString(seconds % 60L);
        long minutes = seconds / 60;
        final String minutesS = (minutes % 60L) < 10L ? "0" + (minutes % 60L) : Long.toString(minutes % 60L);
        long hours = minutes / 60;
        final String hoursS = Long.toString(hours % 60L);

        final String finishTimeS = finishTime == 0 ? "unfinished"
                : hoursS + ":" + minutesS + ":" + secondsS + "." + millisecondsS;
    %>
        <td><%=finishTime%> (<%=finishTimeS%>)</td>
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