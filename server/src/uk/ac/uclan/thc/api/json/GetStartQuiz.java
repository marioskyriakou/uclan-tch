/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.uclan.thc.api.json;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Initiates a new quiz session for the specified category. The session is automatically created if no session is
 * already available for the given parameters combination.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 21:49
 */
public class GetStartQuiz extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code"); // undocumented parameter; used only for testing a category before it becomes active

        final String playerName = request.getParameter("playerName");
        final String appID      = request.getParameter("appID");
        final String categoryUUID = request.getParameter("categoryUUID");

        if(playerName == null || playerName.isEmpty() || appID == null || appID.isEmpty() || categoryUUID == null || categoryUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'"));
        }
        else
        {
            final Category category = CategoryFactory.getCategory(categoryUUID);
            if(category == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getJsonStatus("Unknown category ID", "The specified category ID could not be found"));
            }
            else
            {
                final boolean showInactive = code != null && code.equals(category.getCode());
                if(!category.isActiveNow() && !showInactive)
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getJsonStatus("Inactive category", "The specified category is not active right now"));
                }
                else
                {
                    final String sessionUUID = SessionFactory.getOrCreateSession(playerName, appID, categoryUUID);

                    if(sessionUUID == null)
                    {
                        // report that the given playerName was already used
                        printWriter.println(Protocol.getJsonStatus("Invalid playerName", "The specified playerName is already in use (try a different one)"));
                    }
                    else
                    {
                        final StringBuilder reply = new StringBuilder("{").append(EOL);
                        reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status
                        reply.append("  \"sessionUUID\": \"" + sessionUUID + "\"").append(EOL); // OK status
                        reply.append("}").append(EOL);

                        printWriter.println(reply.toString()); // normal JSON output
                    }
                }
            }
        }
    }
}