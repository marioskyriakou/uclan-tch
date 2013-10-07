package uk.ac.uclan.thc.api;

import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 19:27
 */
public class Protocol
{
    public static final Logger log = Logger.getLogger(Protocol.class.getCanonicalName());

    public static final String EOL = System.getProperty("line.separator");

    public static final String OUTPUT_TYPE              = "type";
    public static final String OUTPUT_TYPE_CSV          = "csv";
    public static final String OUTPUT_TYPE_JSON         = "json";

    static public String getCsvStatus(final String status, final String message)
    {
        return status + "," + message;
    }

    static public String getJsonStatus(final String status, final String message)
    {
        return "{ \"status\": \"" + status + "\", \"message\": \"" + message + "\" }";
    }
}