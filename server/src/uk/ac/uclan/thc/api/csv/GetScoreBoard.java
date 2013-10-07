package uk.ac.uclan.thc.api.csv;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Retrieves the current score board which includes all the players for the category of the specified session.
 *
 * <p>Parameters</p>
 * <ul>
 * <li>URL: '/api/csv/scoreBoard'.</li>
 * <li>session: the session ID, typically retrieved after calling {@link uk.ac.uclan.thc.api.csv.GetStartQuiz}</li>
 * </ul>
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Everything OK
 *         Status: "OK", Message: ""
 *         Second line: the score
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>coolApp2,MyUser,0
 *         <br/>coolApp1,Nearchos,30
 *         <br/>MyApp,Adam,10
 *         <br/>coolApp,Nearchos,30
 *         </code>
 *     </li>
 *     <li>
 *         When one or more parameters are missing
 *         Status: "Invalid or missing parameters", Message: "Valid parameter list 'scoreBoard?session=...'"
 *         Example:
 *         <code>
 *         <br/>Invalid or missing parameters,Valid parameter list 'scoreBoard?session=...'
 *         </code>
 *     </li>
 *     <li>
 *         When the session is unknown
 *         Status: "Unknown session", Message: "The specified session ID could not be found"
 *         Example:
 *         <code>
 *         <br/>Unknown session,The specified session ID could not be found
 *         </code>
 *     </li>
 * </ul>
 *
 * User: Nearchos Paspallis
 * Date: 03/10/13
 * Time: 22:34
 */
public class GetScoreBoard extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String sessionUUID = request.getParameter("session");
        final boolean sorted = request.getParameter("sorted") != null; // just defining the parameter is sufficient

        if(sessionUUID == null)
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Invalid or missing parameters", "Valid parameter list 'scoreBoard?session=...'"));
        }
        else
        {
            final Session session = SessionFactory.getSession(sessionUUID);

            if(session == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getCsvStatus("Unknown session", "The specified session ID could not be found"));
            }
            else
            {
                final String categoryUUID = session.getCategoryUUID();
                final Vector<Session> sessions = SessionFactory.getSessionsByCategoryUUID(categoryUUID, sorted);

                final StringBuilder reply = new StringBuilder();
                reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status
                for(final Session sessionInBoard : sessions)
                {
                    reply.append(sessionInBoard.getAppID()).append(",").append(sessionInBoard.getPlayerName()).append(",").append(sessionInBoard.getScore()).append(EOL);
                }

                printWriter.println(reply.toString()); // normal CSV output
            }
        }
    }
}