package uk.ac.uclan.thc.model;

/**
 * User: Nearchos Paspallis
 * Date: 25/09/13
 * Time: 21:46
 */
public class LocationFingerprint
{
    private final String uuid;
    private final long timestamp;
    private final double lat;
    private final double lng;
    private final String sessionUUID;

    public LocationFingerprint(final String uuid, final long timestamp, final double lat, final double lng, final String sessionUUID)
    {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
        this.sessionUUID = sessionUUID;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public String getSessionUUID()
    {
        return sessionUUID;
    }
}