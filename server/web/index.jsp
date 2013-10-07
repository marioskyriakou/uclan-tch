<%--
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 09:48
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <link rel="stylesheet" type="text/css" href="uclan_thc.css">
        <title>UCLan - Treasure Hunt Challenge</title>
    </head>

    <body>

        <div style="background-image: url(background_stripes.png); height: 158px; margin-bottom:0;">
            <div style="width:800px;margin:0 auto;padding-top:70px;padding-bottom:10px;">
                <div style="margin-left: 10px; float: left;">
                    <a href="http://www.uclan.ac.uk">
                        <img alt="University of Central Lancashire (UCLan)"
                             align="left"
                             src="uclan.png"/></a>
                    <h1>Treasure Hunt Challenge</h1>

                </div>
            </div>
        </div>

        <div style="width:800px;margin:0 auto;padding-top:20px;padding-bottom:10px;padding-right:10px;a link: #99cc00">

            <p>
                This app provides the server-side functionality for the <i>Treasure Hunt Challenge</i> (or TCH).
                This challenge is undertaken by students pursuing the B.Sc. (Hons) in Computing degree at <a href="http://www.uclancyprus.ac.cy">UCLan Cyprus</a>, as part of module CO1111 (Computing Skills).
                The app is deployed on Google's <a href="http://cloud.google.com/appengine">app-engine</a>.
            </p>

            <p>
                The concept of the challenge is largely based on the equivalent <a href="http://pops.uclan.ac.uk/index.php/ujpr/article/view/123">Four-week challenge</a>, originally created at <a href="http://www.uclan.ac.uk">UCLan</a> in Preston.
                Given the API description of the service, the students are asked to develop Android-based mobile apps using <a href="http://appinventor.mit.edu">AppInventor</a>.
                The main goal is to provide students with an overview of key practical aspects of computing.
            </p>

            <hr/>

            <p>
                <a target="_blank" href="/admin" class="uclan_btn">Administration</a>
                <br/>
                <span style="color:#922;">Use this link if you want to setup your own challenge (requires permission).</span>
            </p>

            <hr/>

            <p>
                <a href="/guide" class="uclan_btn">API guidelines</a>
                <br/>
                <span style="color:#922;">Use this link to learn about the API of the TCH.</span>
            </p>

            <hr/>

            <p>
                <a href="contact.jsp">Contact us</a>
            </p>

        </div>

    </body>

</html>