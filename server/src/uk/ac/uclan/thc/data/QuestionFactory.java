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
import uk.ac.uclan.thc.model.Question;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 11:40 AM
 */
public class QuestionFactory
{
    public static final Logger log = Logger.getLogger(QuestionFactory.class.getCanonicalName());

    public static final String KIND = "Question";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_CATEGORY_UUID = "question_category_uuid";
    public static final String PROPERTY_SEQ_NUMBER = "question_seq_number";
    public static final String PROPERTY_TEXT = "question_text";
    public static final String PROPERTY_CORRECT_ANSWER = "question_correct_answer";
    public static final String PROPERTY_LATITUDE = "question_latitude";
    public static final String PROPERTY_LONGITUDE = "question_longitude";

    static public Question getQuestion(final String keyAsString)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(questionEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    static public Vector<Question> getAllQuestionsForCategoryOrderedBySeqNumber(final String categoryUUID)
    {
        final Vector<Question> questions = new Vector<Question>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);
        final Query query = new Query(KIND).addSort(PROPERTY_SEQ_NUMBER, Query.SortDirection.ASCENDING);
        query.setFilter(filterCategory);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            questions.add(getFromEntity(entity));
        }

        return questions;
    }

    static public Key addQuestion(
            final String categoryUuid,
            final int seqNumber,
            final String text,
            final String correctAnswer,
            final double latitude,
            final double longitude)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity categoryEntity = new Entity(KIND);
        categoryEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUuid);
        categoryEntity.setProperty(PROPERTY_TEXT, text);
        categoryEntity.setProperty(PROPERTY_SEQ_NUMBER, seqNumber);
        categoryEntity.setProperty(PROPERTY_CORRECT_ANSWER, correctAnswer);
        categoryEntity.setProperty(PROPERTY_LATITUDE, latitude);
        categoryEntity.setProperty(PROPERTY_LONGITUDE, longitude);

        return datastoreService.put(categoryEntity);
    }

    static public void editQuestion(
            final String uuid,
            final String categoryUUID,
            final int seqNumber,
            final String text,
            final String correctAnswer,
            final double latitude,
            final double longitude)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            questionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
            questionEntity.setProperty(PROPERTY_SEQ_NUMBER, seqNumber);
            questionEntity.setProperty(PROPERTY_TEXT, text);
            questionEntity.setProperty(PROPERTY_CORRECT_ANSWER, correctAnswer);
            questionEntity.setProperty(PROPERTY_LATITUDE, latitude);
            questionEntity.setProperty(PROPERTY_LONGITUDE, longitude);
            datastoreService.put(questionEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Question getFromEntity(final Entity entity)
    {
        return new Question(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_CATEGORY_UUID),
                (Long) entity.getProperty(PROPERTY_SEQ_NUMBER),
                (String) entity.getProperty(PROPERTY_TEXT),
                (String) entity.getProperty(PROPERTY_CORRECT_ANSWER),
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE));
    }
}