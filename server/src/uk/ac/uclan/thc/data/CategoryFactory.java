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
import uk.ac.uclan.thc.model.Category;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class CategoryFactory
{
    public static final Logger log = Logger.getLogger(CategoryFactory.class.getCanonicalName());

    public static final String KIND = "Category";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_NAME = "category_name";
    public static final String PROPERTY_CREATED_BY = "category_created_by";
    public static final String PROPERTY_VALID_FROM = "category_from";
    public static final String PROPERTY_VALID_UNTIL = "category_until";

    static public Category getCategory(final String keyAsString)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity categoryEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(categoryEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    public static final String ALL_CATEGORIES = "all-categories";

    static public Vector<Category> getAllCategories()
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_NAME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Category> categories = new Vector<Category>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            categories.add(getFromEntity(entity));
        }

        return categories;
    }

    static public Key addCategory(final String name, final String createdBy, final long from, final long until)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity categoryEntity = new Entity(KIND);
        categoryEntity.setProperty(PROPERTY_NAME, name);
        categoryEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
        categoryEntity.setProperty(PROPERTY_VALID_FROM, from);
        categoryEntity.setProperty(PROPERTY_VALID_UNTIL, until);

        return datastoreService.put(categoryEntity);
    }

    static public void editCategory(final String uuid, final String name, final String createdBy, final long from, final long until)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity categoryEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            categoryEntity.setProperty(PROPERTY_NAME, name);
            categoryEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
            categoryEntity.setProperty(PROPERTY_VALID_FROM, from);
            categoryEntity.setProperty(PROPERTY_VALID_UNTIL, until);
            datastoreService.put(categoryEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public void deleteCategory(final String uuid)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.delete(KeyFactory.stringToKey(uuid));
    }

    static public Category getFromEntity(final Entity entity)
    {
        return new Category(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_NAME),
                (String) entity.getProperty(PROPERTY_CREATED_BY),
                (Long) entity.getProperty(PROPERTY_VALID_FROM),
                (Long) entity.getProperty(PROPERTY_VALID_UNTIL));
    }
}