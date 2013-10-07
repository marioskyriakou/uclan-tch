package uk.ac.uclan.thc.model;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class Session
{
    private final String uuid;
    private final String playerName;
    private final String appID;
    private final String categoryUUID;
    private final String currentQuestionUUID;
    private final long score;

    public Session(final String uuid,
                   final String playerName,
                   final String appID,
                   final String categoryUUID,
                   final String currentQuestionUUID,
                   final long score)
    {
        this.uuid = uuid;
        this.playerName = playerName;
        this.appID = appID;
        this.categoryUUID = categoryUUID;
        this.currentQuestionUUID = currentQuestionUUID;
        this.score = score;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getAppID()
    {
        return appID;
    }

    public String getCategoryUUID()
    {
        return categoryUUID;
    }

    public String getCurrentQuestionUUID()
    {
        return currentQuestionUUID;
    }

    public long getScore()
    {
        return score;
    }
}