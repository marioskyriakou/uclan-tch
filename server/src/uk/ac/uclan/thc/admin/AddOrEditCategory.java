package uk.ac.uclan.thc.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.UserEntity;
import uk.ac.uclan.thc.model.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 11:55
 */
public class AddOrEditCategory extends HttpServlet
{
    public static final Logger log = Logger.getLogger(AddOrEditCategory.class.getCanonicalName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print("You must sign in first");
        }
        else
        {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                String uuid = request.getParameter(CategoryFactory.PROPERTY_UUID);
                final String name = request.getParameter(CategoryFactory.PROPERTY_NAME);
                final String createdBy = user.getEmail();
                long validFrom;
                long validUntil;
                try
                {
                    validFrom = Category.SIMPLE_DATE_FORMAT.parse(request.getParameter(CategoryFactory.PROPERTY_VALID_FROM)).getTime();
                    validUntil = Category.SIMPLE_DATE_FORMAT.parse(request.getParameter(CategoryFactory.PROPERTY_VALID_UNTIL)).getTime();
                }
                catch (ParseException pe)
                {
                    validFrom = 0L;
                    validUntil = 0L;
                    log.warning(pe.getMessage());
                }

                if(uuid != null && !uuid.isEmpty()) // editing existing category
                {
                    CategoryFactory.editCategory(uuid, name, createdBy, validFrom, validUntil);
                }
                else // adding a new category
                {
                    final Key key = CategoryFactory.addCategory(name, createdBy, validFrom, validUntil);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("/admin/category?uuid=" + uuid);
            }
        }
    }
}
