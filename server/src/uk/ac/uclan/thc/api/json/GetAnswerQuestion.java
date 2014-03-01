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

package uk.ac.uclan.thc.api.json;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.LocationFingerprintFactory;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.LocationFingerprint;
import uk.ac.uclan.thc.model.Question;
import uk.ac.uclan.thc.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Submits a proposed answer for the current question. A correct answer is rewarded with +10 points.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 22:21
 */
public class GetAnswerQuestion extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        // if magic is specified and equals {@link MAGIC}, then show inactive categories too - used by the examiner
        final String sessionUUID = request.getParameter("session");
        final String answer = request.getParameter("answer");

        if(sessionUUID == null || sessionUUID.isEmpty() || answer == null || answer.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'answerQuestion?answer=...&session=...'"));
        }
        else
        {
            final Session session = SessionFactory.getSession(sessionUUID);
            if(session == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getJsonStatus("Unknown session", "The specified session ID could not be found"));
            }
            else
            {
                final String currentQuestionUUID = session.getCurrentQuestionUUID();
                final Question currentQuestion = QuestionFactory.getQuestion(currentQuestionUUID);
                final String correctAnswer = currentQuestion.getCorrectAnswer();

                final StringBuilder reply = new StringBuilder("{").append(EOL);
                reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status

                final String feedback;

                // first check if the answer is correct
                if(!answer.equalsIgnoreCase(correctAnswer))
                {
                    SessionFactory.updateScoreAndKeepSessionToSameQuestion(sessionUUID, currentQuestion.getWrongScore());
                    feedback = "incorrect";
                }
                else // answer is correct
                {
                    // check the location
                    final LocationFingerprint lastLocationFingerprint = LocationFingerprintFactory.getLastLocationFingerprintBySessionUUID(sessionUUID);
                    if(!currentQuestion.isCorrectLocation(lastLocationFingerprint))
                    {
                        SessionFactory.updateScoreAndKeepSessionToSameQuestion(sessionUUID, currentQuestion.getWrongScore());
                        feedback = "unknown or incorrect location";
                    }
                    else
                    {
                        // correct answer and location. check if this was the last question or not
                        final boolean hasMoreQuestions = SessionFactory.updateScoreAndProgressSessionToNextQuestion(sessionUUID, currentQuestion.getCorrectScore());
                        if(hasMoreQuestions) // unfinished
                        {
                            feedback = "correct,unfinished";
                        }
                        else // finished
                        {
                            feedback = "correct,finished";
                        }
                    }
                }

                reply.append("  \"feedback\": \"").append(feedback).append("\"").append(EOL); // OK status
                reply.append("}").append(EOL);

                printWriter.println(reply.toString()); // normal JSON output
            }
        }
    }
}