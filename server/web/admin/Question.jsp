<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Question" %>
<%@ page import="java.util.Vector" %>
<%@ page import="uk.ac.uclan.thc.data.QuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %>
<%--
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 11:59
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Question</title>
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
        String key = request.getParameter("uuid");
        final Question question = QuestionFactory.getQuestion(key);
%>
        <p><a href="/admin/category?uuid=<%=question.getCategoryUUID()%>">Back to category <%=question.getCategoryUUID()%></a></p>

        <form action="/admin/edit-question" method="post" onsubmit="editQuestionButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>SEQ NUMBER</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_SEQ_NUMBER%>" value="<%=question.getSeqNumber()%>"/></td>
                </tr>
                <tr>
                    <th>TEXT</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_TEXT %>" value="<%=question.getText()%>"/></td>
                </tr>
                <tr>
                    <th>CORRECT ANSWER</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_CORRECT_ANSWER %>" value="<%=question.getCorrectAnswer()%>"/></td>
                </tr>
                <tr>
                    <th>LATITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LATITUDE %>" value="<%=question.getLatitude()%>"/></td>
                </tr>
                <tr>
                    <th>LONGITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LONGITUDE %>" value="<%=question.getLongitude()%>"/></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit question" name="editQuestionButton"/></div>
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID%>" value="<%= question.getUUID() %>" />
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_CATEGORY_UUID %>" value="<%= question.getCategoryUUID() %>" />
        </form>

        <hr/>

        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete question" name="deleteButton"/></div>
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%= question.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/category?uuid=" + question.getCategoryUUID(), "UTF-8") %>"/>
        </form>

        <%
    }
%>
    </body>

</html>