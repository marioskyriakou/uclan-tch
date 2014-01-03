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

package org.codecyprus.android_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nearchos Paspallis on 27/12/13.
 */
public class Preferences
{
    public static final String TAG = "org.codecyprus.android_client.Preferences";

    public static final String SHARED_PREFERENCES_FILE = "shared_preferences";

    static private SharedPreferences getSharedPreferences(final Context context)
    {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
    }

    public static final String SESSION_KEYS = "session_keys";

    static public Set<SerializableSession> getAllSessions(final Context context)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final Set<String> jsonSessions = sharedPreferences.getStringSet(SESSION_KEYS, new HashSet<String>());

        final Set<SerializableSession> allSessions = new HashSet<SerializableSession>();
        for(final String jsonSession : jsonSessions)
        {
            allSessions.add(SerializableSession.fromJSON(jsonSession));
        }

        return allSessions;
    }

    static public void addSession(final Context context, final SerializableSession serializableSession)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        final Set<String> jsonSessions = sharedPreferences.getStringSet(SESSION_KEYS, new HashSet<String>());
        jsonSessions.add(serializableSession.getCategoryUUID());

        sharedPreferencesEditor.putString(serializableSession.getCategoryUUID(), SerializableSession.toJSON(serializableSession));

        sharedPreferencesEditor.putStringSet(SESSION_KEYS, jsonSessions);
        sharedPreferencesEditor.commit();
    }

    static public void clearActiveSession(final Context context)
    {
        final SerializableSession activeSerializableSession = getActiveSession(context);
        clearSession(context, activeSerializableSession);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(ACTIVE_SESSION, null);
        sharedPreferencesEditor.commit();
    }

    static public void clearSession(final Context context, final SerializableSession serializableSession)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        final Set<String> jsonSessions = sharedPreferences.getStringSet(SESSION_KEYS, new HashSet<String>());
        jsonSessions.remove(serializableSession.getCategoryUUID());

        sharedPreferencesEditor.remove(serializableSession.getCategoryUUID());

        sharedPreferencesEditor.putStringSet(SESSION_KEYS, jsonSessions);
        sharedPreferencesEditor.commit();
    }

    static public SerializableSession getSession(final Context context, final String categoryUUID)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final String sessionAsJSON = sharedPreferences.getString(categoryUUID, "");

        return SerializableSession.fromJSON(sessionAsJSON);
    }

    public static final String ACTIVE_SESSION = "active_session";

    static public SerializableSession getActiveSession(final Context context)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final String sessionAsJSON = sharedPreferences.getString(ACTIVE_SESSION, "");

        return sessionAsJSON.isEmpty() ? null : SerializableSession.fromJSON(sessionAsJSON);
    }

    static public void setActiveSession(final Context context, final SerializableSession serializableSession)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putString(ACTIVE_SESSION, SerializableSession.toJSON(serializableSession));
        sharedPreferencesEditor.commit();
    }
}