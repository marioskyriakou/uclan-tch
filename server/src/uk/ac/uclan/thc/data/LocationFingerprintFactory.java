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

package uk.ac.uclan.thc.data;

import com.google.appengine.api.datastore.*;
import uk.ac.uclan.thc.model.LocationFingerprint;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class LocationFingerprintFactory
{
    public static final Logger log = Logger.getLogger(LocationFingerprintFactory.class.getCanonicalName());

    public static final String KIND = "LocationFingerprint";

    public static final String PROPERTY_TIMESTAMP = "timestamp";
    public static final String PROPERTY_LATITUDE = "lat";
    public static final String PROPERTY_LONGITUDE = "lng";
    public static final String PROPERTY_SESSION_UUID = "session_uuid";

    static public LocationFingerprint getLocationFingerprint(final String keyAsString)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity locationFingerprintEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(locationFingerprintEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    static public Vector<LocationFingerprint> getLocationFingerprintsBySessionUUID(final String sessionUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_SESSION_UUID,
                Query.FilterOperator.EQUAL,
                sessionUUID);
        final Query query = new Query(KIND);
        query.setFilter(filterCategory);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<LocationFingerprint> locationFingerprints = new Vector<LocationFingerprint>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            locationFingerprints.add(getFromEntity(entity));
        }

        return locationFingerprints;
    }

    /**
     * It returns the most updated {@link LocationFingerprint} available. Could be null if no fingerprint is
     * available for the given session.
     *
     * @param sessionUUID
     * @return the most recent {@link LocationFingerprint} if it exists, otherwise NULL
     */
    static public LocationFingerprint getLastLocationFingerprintBySessionUUID(final String sessionUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_SESSION_UUID,
                Query.FilterOperator.EQUAL,
                sessionUUID);
        final Query query = new Query(KIND);
        query.setFilter(filterCategory).addSort(PROPERTY_TIMESTAMP, Query.SortDirection.DESCENDING);

        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        final Iterator<Entity> iterator = preparedQuery.asIterable().iterator();
        if(iterator.hasNext())
        {
            return getFromEntity(iterator.next());
        }
        else
        {
            return null;
        }
    }

    static public Key addLocationFingerprint(final double lat, final double lng, final String sessionUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity locationFingerprintEntity = new Entity(KIND);
        locationFingerprintEntity.setProperty(PROPERTY_TIMESTAMP, System.currentTimeMillis());
        locationFingerprintEntity.setProperty(PROPERTY_LATITUDE, lat);
        locationFingerprintEntity.setProperty(PROPERTY_LONGITUDE, lng);
        locationFingerprintEntity.setProperty(PROPERTY_SESSION_UUID, sessionUUID);

        return datastoreService.put(locationFingerprintEntity);
    }

    static public LocationFingerprint getFromEntity(final Entity entity)
    {
        return new LocationFingerprint(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_TIMESTAMP),
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE),
                (String) entity.getProperty(PROPERTY_SESSION_UUID));
    }
}