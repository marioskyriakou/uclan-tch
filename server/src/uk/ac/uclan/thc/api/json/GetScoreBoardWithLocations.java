package uk.ac.uclan.thc.api.json;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.LocationFingerprintFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.LocationFingerprint;
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
 * Similar to {@link uk.ac.uclan.thc.api.csv.GetScoreBoard} but returns JSON-formatted data.
 *
 * User: Nearchos Paspallis
 * Date: 03/10/13
 * Time: 22:34
 */
public class GetScoreBoardWithLocations extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        String categoryUUID = request.getParameter("categoryUUID");
        final boolean sorted = request.getParameter("sorted") != null; // just defining the parameter is sufficient

        if(categoryUUID == null)
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'scoreBoard?categoryUUID=...'"));
        }
        else
        {
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

            final Vector<Session> sessions = SessionFactory.getSessionsByCategoryUUID(categoryUUID, sorted);

            final StringBuilder reply = new StringBuilder("{").append(EOL);
            reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status
            reply.append("  \"message\": \"\"").append(",").append(EOL); // OK status
            reply.append("  \"scoreBoard\": [").append(EOL); // OK status
            for(int i = 0; i < sessions.size(); i++)
            {
                final Session sessionInCategory = sessions.elementAt(i);
                final LocationFingerprint locationFingerprint = LocationFingerprintFactory.getLastLocationFingerprintBySessionUUID(sessionInCategory.getUUID());
                double lat = locationFingerprint == null ? 0d : locationFingerprint.getLat();
                double lng = locationFingerprint == null ? 0d : locationFingerprint.getLng();

                reply.append("  {").append(EOL);
                reply.append("    \"appID\": \"").append(sessionInCategory.getAppID()).append("\",").append(EOL);
                reply.append("    \"playerName\": \"").append(sessionInCategory.getPlayerName()).append("\",").append(EOL);
                reply.append("    \"score\": ").append(sessionInCategory.getScore()).append(",").append(EOL);
                reply.append("    \"finishTime\": ").append(sessionInCategory.getFinishTime()).append(",").append(EOL);
                reply.append("    \"lat\": ").append(lat).append(",").append(EOL);
                reply.append("    \"lng\": ").append(lng).append("").append(EOL);
                reply.append("  }").append(i < sessions.size() - 1 ? "," : "").append(EOL);
            }
            reply.append("  ]").append(EOL);
            reply.append("}").append(EOL);

            printWriter.println(reply.toString()); // normal CSV output
        }
    }
}