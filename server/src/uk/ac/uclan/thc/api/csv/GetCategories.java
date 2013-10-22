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

package uk.ac.uclan.thc.api.csv;

import static uk.ac.uclan.thc.api.Protocol.EOL;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.model.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Retrieves the list of available categories.
 *
 * URL: '/api/csv/categories'.
 * No parameters expected.
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Everything OK
 *         Status: "OK", Message: ""
 *         Followed by lines of: "uuid,name"
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>agtzfnVjbGFuLXRoY3IVCxIIQ2F0ZWdvcnkYgICAgIC6hwoM,Treasure hunt 2013
 *         <br/>agtzfnVjbGFuLXRoY3IVCxIIQ2F0ZWdvcnkYgICAgICWvQoM,Treasure hunt 2014
 *         </code>
 *     </li>
 *     <li>
 *         When no categories are found on the server
 *         Status: "Empty", Message: "No categories found"
 *         Example:
 *         <code>
 *         <br/>Empty,No categories found
 *         </code>
 *     </li>
 * </ul>
 *
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 09:51
 */
public class GetCategories extends HttpServlet
{
    public static final String MAGIC = "co1111"; // todo make this parameterized

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        // if magic is specified and equals {@link MAGIC}, then show inactive categories too - used by the examiner
        final String magic = request.getParameter("magic");
        final boolean showInactive = MAGIC.equals(magic);

        final Vector<Category> categories = CategoryFactory.getAllCategories();
        if(categories.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Empty", "No categories found"));
        }
        else
        {
            final StringBuilder reply = new StringBuilder();

            reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status
            for(final Category category : categories)
            {
//                final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(category.getUUID());
//                // returns: uuid,name,createdBy,validFrom,validTo,noOfQuestions
//                reply.append(category.getUUID()).append(",")
//                        .append(category.getName()).append(",")
//                        .append(category.getCreatedBy()).append(",")
//                        .append(category.getValidFromAsString()).append(",")
//                        .append(category.getValidUntilAsString()).append(",")
//                        .append(questions.size()).append(EOL);
                // returns: uuid,name

                if(showInactive || category.isActiveNow())
                {
                    reply.append(category.getUUID()).append(",").append(category.getName()).append(EOL);
                }
            }

            printWriter.println(reply.toString()); // normal CSV output
        }
    }
}