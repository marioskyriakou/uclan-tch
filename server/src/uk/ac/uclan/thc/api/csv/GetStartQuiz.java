package uk.ac.uclan.thc.api.csv;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;
import uk.ac.uclan.thc.model.Question;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Initiates a new quiz session for the specified category. The session is automatically created if no session is
 * already available for the given parameters combination.
 *
 * <p>Parameters</p>
 * <ul>
 * <li>URL: '/api/csv/startQuiz'.</li>
 * <li>playerName: so it can appear on the leader board</li>
 * <li>appID: typically, the name of your team</li>
 * <li>categoryUUID: the UUID of a valid, active category (you can get this by using '/api/csv/categories', see {@link GetCategories}).</li>
 * </ul>
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Everything OK
 *         Status: "OK", Message: ""
 *         Second line: the 'session' ID to be used in subsequent calls (e.g. in {@link GetCurrentQuestion} or
 *         {@link GetUpdateLocation}
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>agtzfnVjbGFuLXRoY3IVCxIIQ2F0ZWdvcnkYgICAgIC6hwkM
 *         </code>
 *     </li>
 *     <li>
 *         When one or more parameters are missing
 *         Status: "Invalid or missing parameters", Message: "Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'"
 *         Example:
 *         <code>
 *         <br/>Invalid or missing parameters,Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'
 *         </code>
 *     </li>
 *     <li>
 *         When the category is unknown
 *         Status: "Unknown category ID", Message: "The specified category ID could not be found"
 *         Example:
 *         <code>
 *         <br/>Unknown category ID,The specified category ID could not be found
 *         </code>
 *     </li>
 *     <li>
 *         When the category is not active
 *         Status: "Inactive category", Message: "The specified category is not active currently"
 *         Example:
 *         <code>
 *         <br/>Inactive category,The specified category is not active right now
 *         </code>
 *     </li>
 * </ul>
 *
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 09:51
 */
public class GetStartQuiz extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String playerName = request.getParameter("playerName");
        final String appID      = request.getParameter("appID");
        final String categoryUUID = request.getParameter("categoryUUID");

        if(playerName == null || playerName.isEmpty() || appID == null || appID.isEmpty() || categoryUUID == null || categoryUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Invalid or missing parameters", "Valid parameter list 'starQuiz?playerName=...&appID=...&categoryUUID=...'"));
        }
        else
        {
            final Category category = CategoryFactory.getCategory(categoryUUID);
            if(category == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getCsvStatus("Unknown category ID", "The specified category ID could not be found"));
            }
            else
            {
                if(!category.isActiveNow())
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getCsvStatus("Inactive category", "The specified category is not active right now"));
                }
                else
                {
                    final StringBuilder reply = new StringBuilder();
                    reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status

                    final String sessionUUID = SessionFactory.getOrCreateSession(playerName, appID, categoryUUID);
                    reply.append(sessionUUID).append(EOL);

                    printWriter.println(reply.toString()); // normal CSV output
                }
            }
        }
    }
}