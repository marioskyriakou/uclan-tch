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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

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
    private static final Logger log = Logger.getLogger(GetStartQuiz.class.getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code"); // undocumented parameter; used only for testing a category before it becomes active

        final String playerName = request.getParameter("playerName");
        final String appID      = request.getParameter("appID");
        final String categoryUUID = request.getParameter("categoryUUID");
        final String name1 = request.getParameter("name1");
        final String email1 = request.getParameter("email1");
        final String name2 = request.getParameter("name2");
        final String email2 = request.getParameter("email2");

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
                        // everything ok

                        // first send an email
                        if(name1 != null && !name1.isEmpty()) sendEmail(category.getCreatedBy(), category.getName(),
                                                                        playerName, appID, categoryUUID, name1,
                                                                        email1, name2, email2, request.getRemoteAddr());

                        // next prepare and send the reply
                        final StringBuilder reply = new StringBuilder("{").append(EOL);
                        reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status
                        reply.append("  \"sessionUUID\": \"").append(sessionUUID).append("\"").append(EOL); // OK status
                        reply.append("}").append(EOL);

                        printWriter.println(reply.toString()); // normal JSON output
                    }
                }
            }
        }
    }

    private void sendEmail(final String categoryCreatedByEmail, final String categoryName,
                           final String teamName, final String appID, final String categoryUUID, final String name1,
                           final String email1, final String name2, final String email2, final String senderIP)
    {
        final Session session = Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email1, name1, "utf-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(categoryCreatedByEmail, categoryName));
            message.setSubject("Treasure Hunt - " + appID + " from: " + email1);
            message.setText(
                    "Competition entry: "
                            + "\nApp id: " + appID
                            + "\nTeam name: " + teamName
                            + "\nName1: " + name1
                            + "\nEmail1: " + email1
                            + "\nName2: " + name2
                            + "\nEmail2: " + email2
                            + "\nCategory UUID: " + categoryUUID
                            + "\nSubmitted (UTC): " + new Date()
                            + "\nSender IP: " + senderIP
            );
            Transport.send(message);
        }
        catch (AddressException ae)
        {
            log.severe(ae.getMessage());
        }
        catch (MessagingException me)
        {
            log.severe(me.getMessage());
        }
        catch (UnsupportedEncodingException uee)
        {
            log.severe(uee.getMessage());
        }
    }
}