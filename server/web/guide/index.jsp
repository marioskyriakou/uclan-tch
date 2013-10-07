<%--
  User: Nearchos Paspallis
  Date: 4/10/13
  Time: 09:48
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <link rel="stylesheet" type="text/css" href="../uclan_thc.css">
        <title>API Guide</title>
    </head>

    <body>

    <div style="background-image: url(../background_stripes.png); height: 158px; margin-bottom:0;">
        <div style="width:800px;margin:0 auto;padding-top:70px;padding-bottom:10px;">
            <div style="margin-left: 10px; float: left;">
                <a href="http://uclan-thc.appspot.com">
                    <img alt="University of Central Lancashire (UCLan)"
                         align="left"
                         src="../uclan.png"/></a>
                <h1>Treasure Hunt Challenge</h1>
            </div>
        </div>
    </div>

        <div style="width:800px;margin:0 auto;padding-top:20px;padding-bottom:10px;padding-right:10px;a link: #99cc00">

            <h2>API Guide</h2>

            <p>This guide provides a quick tutorial on how the Treasure Hunt Challenge API can be used.</p>

            <h3>Quick overview</h3>

            <hr/>
            <p><b>/api/csv/categories</b></p>
            <p>
                Retrieves the list of available categories. Can be called at any time without a player name or session ID. Retrieves a list of categories.
                <br/>
                <a href="/guide/categories.html" class="uclan_btn" align="right">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/starQuiz?playerName=...&appID=...&categoryUUID=...</b></p>
            <p>
                Initiates a new quiz session for the specified category. The session is automatically created if no session is already available for the given parameters combination.
                <br/>
                <a href="/guide/startQuiz.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/currentQuestion?session=...</b></p>
            <p>
                Retrieves the current question for the specified session.
                The returned data include the ID of the question, the text of the question, and whether the question requires a lat/lng pair to be answered.
                <br/>
                <a href="/guide/currentQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/answerQuestion?answer=...&session=...</b></p>
            <p>
                Submits a proposed answer for the current question. A correct answer is rewarded with +10 points.
                <br/>
                <a href="/guide/answerQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/updateLocation?session=...&lat=...&lng=...</b></p>
            <p>
                Updates the current position for the specified session. No data is returned.
                <br/>
                <a href="/guide/updateLocation.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/skipQuestion?session=...</b></p>
            <p>
                Skips the current question in the current session. This action costs -5 points.
                <br/>
                <a href="/guide/skipQuestion.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/score?session=...</b></p>
            <p>
                Retrieves the current score for the specified session (i.e. the same user/app).
                <br/>
                <a href="/guide/score.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
            <p><b>/api/csv/scoreBoard?session=...</b></p>
            <p>
                Retrieves the current score for all the players for the category of the specified session.
                <br/>
                <a href="/guide/scoreBoard.html" class="uclan_btn">View details</a>
            </p>

            <hr/>
        </div>

    </body>

</html>