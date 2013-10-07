package uk.ac.uclan.thc.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 11:55
 */
public class AddOrEditQuestion extends HttpServlet
{
    private static final Logger log = Logger.getLogger(AddOrEditQuestion.class.getName());

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
                String uuid = request.getParameter(QuestionFactory.PROPERTY_UUID);
                final String categoryUuid = request.getParameter(QuestionFactory.PROPERTY_CATEGORY_UUID);
                final String seqNumberS = request.getParameter(QuestionFactory.PROPERTY_SEQ_NUMBER);
                int seqNumber = -1;
                try { seqNumber = Integer.parseInt(seqNumberS); } catch (NumberFormatException nfe) { log.warning(nfe.getMessage()); }
                final String text = request.getParameter(QuestionFactory.PROPERTY_TEXT);
                final String correctAnswer = request.getParameter(QuestionFactory.PROPERTY_CORRECT_ANSWER);
                float latitude = 0f;
                try { latitude = Float.parseFloat(request.getParameter(QuestionFactory.PROPERTY_LATITUDE)); } catch (NumberFormatException nfe) { log.warning(nfe.getMessage()); }
                float longitude = 0f;
                try { longitude = Float.parseFloat(request.getParameter(QuestionFactory.PROPERTY_LONGITUDE)); } catch (NumberFormatException nfe) { log.warning(nfe.getMessage()); }

                if(uuid != null && !uuid.isEmpty()) // editing existing category
                {
                    QuestionFactory.editQuestion(uuid, categoryUuid, seqNumber, text, correctAnswer, latitude, longitude);
                }
                else // adding a new category
                {
                    final Key key = QuestionFactory.addQuestion(categoryUuid, seqNumber, text, correctAnswer, latitude, longitude);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("/admin/question?uuid=" + uuid);
            }
        }
    }
}
