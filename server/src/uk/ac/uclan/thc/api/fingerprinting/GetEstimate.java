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

import uk.ac.uclan.thc.api.Protocol;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * 09/02/14 / 15:37.
 */
public class GetEstimate extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String latS = request.getParameter("defaultLat");
        final String lngS = request.getParameter("defaultLng");
        double lat = latS != null ? Double.parseDouble(latS) : 0d;
        double lng = lngS != null ? Double.parseDouble(lngS) : 0d;
        double accuracy = 0d;

        final Map<String, String> ssidNamesToSignal = request.getParameterMap();
        // todo search DB and produce estimate if any

        printWriter.println("{ " +
                "  \"status\": \"OK\", " +
                "  \"message\": \"\", " +
                "  \"lat\": " + lat + ", " + // could be the default value, if any, or zero if no accuracy was achieved
                "  \"lng\": " + lng + ", " + // could be the default value, if any, or zero if no accuracy was achieved
                "  \"accuracy\": " + accuracy + "" + // will be 0..1, where zero means no accuracy, and 1 means 100% accuracy
                "}");
        printWriter.println(Protocol.getJsonStatus("OK", "")); // normal JSON output
    }
}