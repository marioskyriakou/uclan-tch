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

package uk.ac.uclan.thc.api.fingerprinting;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 09/02/14 / 15:37.
 */
public class SubmitTraining extends HttpServlet
{
    public static final Logger log = Logger.getLogger(SubmitTraining.class.getCanonicalName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        if(user == null)
        {
            printWriter.print(Protocol.getJsonStatus("Authentication error", "You must sign in first"));
        }
        else
        {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print(Protocol.getJsonStatus("Authentication error", "User '" + user.getEmail() + "' is not an admin"));
            }
            else
            {
                final long timestamp = System.currentTimeMillis();
                final String createdBy = user.getEmail();

                final Map<String, String> ssidNamesToSignal = request.getParameterMap();
                log.info(" Created by: " + createdBy + " on " + new Date(timestamp));//todo delete
                for(final Map.Entry<String,String> ssidNameToSignal : ssidNamesToSignal.entrySet())
                {
                    log.info(ssidNameToSignal.getKey() + " -> " + ssidNameToSignal.getValue());//todo delete
                    //todo store to DB
                }

                printWriter.println(Protocol.getJsonStatus("OK", "")); // normal JSON output
            }
        }
    }
}