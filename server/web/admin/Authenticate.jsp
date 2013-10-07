<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="uk.ac.uclan.thc.data.UserEntity" %>
<%--
  Date: 8/17/12
  Time: 10:41 AM
--%>
    <p> <h1> UCLan Treasure Hunt Challenge </h1> </p>
<%
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    final String userEmail = user == null ? "Unknown" : user.getEmail();
    UserEntity userEntity = null;
    if (user == null)
    {
%>
    <p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
<%
    }
    else
    {
        userEntity = UserEntity.getUserEntity(user.getEmail());
        if(userEntity == null)
        {
            userEntity = UserEntity.setUserEntity(user.getEmail(), user.getNickname(), false);
        }
%>
    <span><img src="../favicon.png" alt="UCLan"/> Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</span>
<%
    }
%>