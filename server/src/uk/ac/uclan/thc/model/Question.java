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

package uk.ac.uclan.thc.model;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 13:42
 */
public class Question
{
//    public static final String QUESTION_TYPE_MCQ = "mcq";
//    public static final String QUESTION_TYPE_TEXT = "text";
//    public static final String QUESTION_TYPE_PICTURE = "picture";
//
//    public static enum QuestionType
//    {
//        MCQ(QUESTION_TYPE_MCQ), TEXT(QUESTION_TYPE_TEXT), PICTURE(QUESTION_TYPE_PICTURE);
//
//        private final String name;
//
//        QuestionType(final String name)
//        {
//            this.name = name;
//        }
//
//        public String getName()
//        {
//            return name;
//        }
//    }

    private final String uuid;
    private final String categoryUUID;
    private final long seqNumber;
    private final String text;
    private final String correctAnswer;
    private final double latitude;  // keep 0 if irrelevant
    private final double longitude; // keep 0 if irrelevant

    public Question(final String uuid,
                    final String categoryUUID,
                    final long seqNumber,
                    final String text,
                    final String correctAnswer,
                    final double latitude,
                    final double longitude)
    {
        this.uuid = uuid;
        this.categoryUUID = categoryUUID;
        this.seqNumber = seqNumber;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getCategoryUUID()
    {
        return categoryUUID;
    }

    public long getSeqNumber()
    {
        return seqNumber;
    }

    public String getText()
    {
        return text;
    }

    public String getCorrectAnswer()
    {
        return correctAnswer;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public boolean isLocationRelevant()
    {
        return this.latitude != 0.0d && this.longitude != 0.0d;
    }

//    public boolean isCorrectAnswer(final String answer, final double latitude, final double longitude)
//    {
//        if(answer == null) return false;
//
//        final boolean isLocationCorrect = isLocationRelevant() ? distanceTo(latitude, longitude) < DISTANCE_THRESHOLD : true;
//
//        return correctAnswer.equalsIgnoreCase(answer) && isLocationCorrect;
//    }

    public boolean isCorrectLocation(final LocationFingerprint locationFingerprint)
    {
        double latitude = 0d;
        double longitude = 0d;

        if(locationFingerprint != null)
        {
            latitude = locationFingerprint.getLat();
            longitude = locationFingerprint.getLng();
        }

        return isLocationRelevant() ? distanceTo(latitude, longitude) < DISTANCE_THRESHOLD : true;
    }

    public static final float DISTANCE_THRESHOLD = 25; // in meters

    private float distanceTo(final double latitude, final double longitude)
    {
        final double R = 6371f; // earth diameter, in Km
        final double dLat = toRad(this.latitude -latitude);
        final double dLng = toRad(this.longitude - longitude);
        final double latRadian1 = toRad(latitude);
        final double latRadian2 = toRad(this.latitude);

        final double a = Math.sin(dLat/2d) * Math.sin(dLat/2d) + Math.sin(dLng/2d) * Math.sin(dLng/2d) * Math.cos(latRadian1) * Math.cos(latRadian2);
        final double c = 2d * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (float) (R * c);
    }

    private double toRad(final double n)
    {
        return n * Math.PI / 180d;
    }

    @Override public String toString()
    {
        return seqNumber + " (" + text + ")";
    }
}