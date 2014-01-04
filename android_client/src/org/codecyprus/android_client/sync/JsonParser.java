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

package org.codecyprus.android_client.sync;

import static org.codecyprus.android_client.model.Answer.*;

import org.codecyprus.android_client.model.Answer;
import org.codecyprus.android_client.model.Question;
import org.codecyprus.android_client.model.ScoreEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.codecyprus.android_client.model.Category;

import java.util.Vector;

/**
 * Created by Nearchos Paspallis on 22/12/13.
 */
public class JsonParser
{
    public static Category [] parseGetCategories(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        final JSONArray jsonArray = jo.getJSONArray("categories");
        final int size = jsonArray.length();
        final Category [] categories = new Category[size];
        for(int i = 0; i < size; i++)
        {
            final JSONObject categoryObject = jsonArray.getJSONObject(i);
            final String uuid = categoryObject.getString("uuid");
            final String name = categoryObject.getString("name");
            final String validUntil = categoryObject.getString("validUntil");
            categories[i] = new Category(uuid, name, validUntil);
        }

        return categories;
    }

    public static String parseStartQuiz(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        return jo.getString("sessionUUID");
    }

    public static Question parseCurrentQuestion(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        return new Question(jo.getString("question"), jo.getBoolean("isLocationRelevant"));
    }

    public static Answer parseAnswerQuestion(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        final String feedback = jo.getString("feedback");

        if("incorrect".equals(feedback))
        {
            return INCORRECT;
        }
        else if("unknown or incorrect location".equals(feedback))
        {
            return UNKNOWN_OR_INCORRECT_LOCATION;
        }
        else if("correct,unfinished".equals(feedback))
        {
            return CORRECT_UNFINISHED;
        }
        else // assume "correct,finished"
        {
            return CORRECT_FINISHED;
        }
    }

    public static boolean parseSkipQuestion(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        return jo.getBoolean("hasMoreQuestions");
    }

    public static int parseScore(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        return jo.getInt("score");
    }

    public static Vector<ScoreEntry> parseScoreBoard(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }

        final Vector<ScoreEntry> scoreEntries = new Vector<ScoreEntry>();

        final JSONArray scoreEntriesArray = jo.getJSONArray("scoreBoard");
        for(int i = 0; i < scoreEntriesArray.length(); i++)
        {
            final JSONObject scoreEntry = scoreEntriesArray.getJSONObject(i);
            scoreEntries.add(new ScoreEntry(
                    scoreEntry.getString("appID"),
                    scoreEntry.getString("playerName"),
                    scoreEntry.getInt("score"),
                    scoreEntry.getLong("finishTime")));
        }

        return scoreEntries;
    }

    public static void parseUpdateLocation(final String json)
            throws JSONException, JsonParseException
    {
        final JSONObject jo = new JSONObject(json);

        final String status = jo.getString("status");
        if(!"OK".equals(status))
        {
            final String message = jo.getString("message");
            throw new JsonParseException(status, message);
        }
    }
}