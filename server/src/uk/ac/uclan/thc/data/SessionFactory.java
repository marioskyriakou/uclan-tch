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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import uk.ac.uclan.thc.model.*;
import uk.ac.uclan.thc.model.Category;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class SessionFactory
{
    public static final Logger log = Logger.getLogger(SessionFactory.class.getCanonicalName());

    public static final String KIND = "Session";

    public static final String PROPERTY_PLAYER_NAME = "player_name";
    public static final String PROPERTY_APP_ID = "app_id";
    public static final String PROPERTY_CATEGORY_UUID = "category_uuid";
    public static final String PROPERTY_CURRENT_QUESTION_UUID = "current_question_uuid";
    public static final String PROPERTY_SCORE = "score";
    public static final String PROPERTY_FINISH_TIME = "finish_time";

    static public Session getSession(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString)) // first check the cache
        {
            return (Session) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final Session session = getFromEntity(sessionEntity);

                memcacheService.put(keyAsString, session); // update cache

                return session;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
            catch (IllegalArgumentException iae)
            {
                log.warning("Invalid argument " + iae.getMessage());

                return null;
            }
        }
    }

    /**
     * Returns the UUID of the created (or retrieved) {@link Session}.
     *
     * @param playerName the name of the player
     * @param appID the ID of the app
     * @param categoryUUID the UUID of the category
     * @return the UUID of the created (or retrieved) {@link Session}
     */
    static public String getOrCreateSession(final String playerName, final String appID, final String categoryUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        // first check if a session exists for this playerName/appID/categoryUUID
        final Query.Filter filterPlayerName = new Query.FilterPredicate(
                PROPERTY_PLAYER_NAME,
                Query.FilterOperator.EQUAL,
                playerName);
        final Query.Filter filterAppID = new Query.FilterPredicate(
                PROPERTY_APP_ID,
                Query.FilterOperator.EQUAL,
                appID);
        final Query.Filter filterCategoryUUID = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);

        final Query query = new Query(KIND);
        final Query.Filter compositeFilter =
                Query.CompositeFilterOperator.and(filterPlayerName, filterAppID, filterCategoryUUID);
        query.setFilter(compositeFilter);

        final Vector<Session> sessions = new Vector<Session>();

        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            sessions.add(getFromEntity(entity));
        }

        // there should be exactly 0 or 1 sessions available
        if(sessions.isEmpty()) // create new session
        {
            final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);

            // if questions.isEmpty, set text to empty String "" implying finished session
            final String firstQuestionUUID = questions.isEmpty() ? "" : questions.elementAt(0).getUUID();
            final Key key = addSession(playerName, appID, categoryUUID, firstQuestionUUID);
            return KeyFactory.keyToString(key);
        }
        else // there must be exactly 1 session available
        {
            return sessions.get(0).getUUID();
        }
    }

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID)
    {
        return getSessionsByCategoryUUID(categoryUUID, false);
    }

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID, final boolean sorted)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);
        final Query query = new Query(KIND);
        query.setFilter(filterCategory);
        if(sorted)
        {
            query.addSort(PROPERTY_SCORE, Query.SortDirection.DESCENDING);
        }
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Session> sessions = new Vector<Session>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            sessions.add(getFromEntity(entity));
        }

        return sessions;
    }

    /**
     * Updates the specified session by progressing to the next question.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @return true of there is indeed a next question, false otherwise (i.e. if that was the last question)
     */
    static public boolean updateScoreAndProgressSessionToNextQuestion(final String sessionUUID)
    {
        final Session session = getSession(sessionUUID);
        final long newScore = session.getScore() + 10L;

        final String currentQuestionUUID = session.getCurrentQuestionUUID();
        if("".equals(currentQuestionUUID)) // already finished the questions sequence in this session
        {
            return false;
        }
        else
        {
            MemcacheServiceFactory.getMemcacheService().delete(sessionUUID); // invalidate cache entry

            final String categoryUUID = session.getCategoryUUID();
            final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);
            final int numOfQuestions = questions.size();
            for(int i = 0; i < numOfQuestions; i++)
            {
                final Question question = questions.elementAt(i);
                if(question.getUUID().equals(currentQuestionUUID))
                {
                    if(i == numOfQuestions-1) // finished the questions sequence in this session
                    {
                        final Category category = CategoryFactory.getCategory(categoryUUID);
                        final long finishTime = System.currentTimeMillis() - category.getValidFrom();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, "", newScore, finishTime);
                        return false;
                    }
                    else
                    {
                        final String nextQuestionUUID = questions.elementAt(i+1).getUUID();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, nextQuestionUUID, newScore, 0L);
                        return true;
                    }
                }
            }

            // normally, this line would never execute
            log.severe("Error while progressing session with UUID: " + sessionUUID + " to the next question (currentQuestionUUID: " + currentQuestionUUID + ")");
            return false;
        }
    }

    /**
     * Skips the current question in this session by progressing to the next question.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @return true of there is indeed a next question, false otherwise (i.e. if that was the last question)
     */
    static public boolean updateScoreAndSkipSessionToNextQuestion(final String sessionUUID)
    {
        final Session session = getSession(sessionUUID);

        final String currentQuestionUUID = session.getCurrentQuestionUUID();
        if("".equals(currentQuestionUUID)) // already finished the questions sequence in this session
        {
            return false;
        }
        else
        {
            MemcacheServiceFactory.getMemcacheService().delete(sessionUUID); // invalidate cache entry

            final long newScore = session.getScore() - 5L;

            final String categoryUUID = session.getCategoryUUID();
            final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);
            final int numOfQuestions = questions.size();
            for(int i = 0; i < numOfQuestions; i++)
            {
                final Question question = questions.elementAt(i);
                if(question.getUUID().equals(currentQuestionUUID))
                {
                    if(i == numOfQuestions-1) // finished the questions sequence in this session
                    {
                        final Category category = CategoryFactory.getCategory(categoryUUID);
                        final long finishTime = System.currentTimeMillis() - category.getValidFrom();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, "", newScore, finishTime);
                        return false;
                    }
                    else
                    {
                        final String nextQuestionUUID = questions.elementAt(i+1).getUUID();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, nextQuestionUUID, newScore, 0L);
                        return true;
                    }
                }
            }

            // normally, this line would never execute
            log.severe("Error while skipping question in session with UUID: " + sessionUUID + " to the next question (currentQuestionUUID: " + currentQuestionUUID + ")");
            return false;
        }
    }

    static private void updateSessionWithNextQuestionUUIDScoreAndFinishTime(final String sessionUUID,
                                                                  final String nextQuestionUUID,
                                                                  final long score,
                                                                  final long finishTime)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(sessionUUID));
            sessionEntity.setProperty(PROPERTY_CURRENT_QUESTION_UUID, nextQuestionUUID);
            sessionEntity.setProperty(PROPERTY_SCORE, score);
            sessionEntity.setProperty(PROPERTY_FINISH_TIME, finishTime);

            datastoreService.put(sessionEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + sessionUUID);
        }
        catch (IllegalArgumentException iae)
        {
            log.warning("Invalid argument " + iae.getMessage());
        }
    }

    static public Key addSession(final String playerName,
                                 final String appID,
                                 final String categoryUUID,
                                 final String currentQuestionUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity sessionEntity = new Entity(KIND);
        sessionEntity.setProperty(PROPERTY_PLAYER_NAME, playerName);
        sessionEntity.setProperty(PROPERTY_APP_ID, appID);
        sessionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
        sessionEntity.setProperty(PROPERTY_CURRENT_QUESTION_UUID, currentQuestionUUID);
        sessionEntity.setProperty(PROPERTY_SCORE, 0);
        sessionEntity.setProperty(PROPERTY_FINISH_TIME, 0);

        return datastoreService.put(sessionEntity);
    }

    static public Session getFromEntity(final Entity entity)
    {
        return new Session(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_PLAYER_NAME),
                (String) entity.getProperty(PROPERTY_APP_ID),
                (String) entity.getProperty(PROPERTY_CATEGORY_UUID),
                (String) entity.getProperty(PROPERTY_CURRENT_QUESTION_UUID),
                (Long) entity.getProperty(PROPERTY_SCORE),
                (Long) entity.getProperty(PROPERTY_FINISH_TIME));
    }
}