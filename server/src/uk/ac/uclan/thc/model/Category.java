package uk.ac.uclan.thc.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 10:06
 */
public class Category
{
    private String uuid;
    private String name;
    private String createdBy;
    private long validFrom;
    private long validUntil;

    public Category(final String uuid, final String name, final String createdBy, final long validFrom, final long validUntil)
    {
        this.uuid = uuid;
        this.name = name;
        this.createdBy = createdBy;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public long getValidFrom()
    {
        return validFrom;
    }

    public long getValidUntil()
    {
        return validUntil;
    }

    public boolean isActiveNow()
    {
        final long now = System.currentTimeMillis();
        return now >= validFrom && now <= validUntil;
    }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public String getValidFromAsString()
    {
        return SIMPLE_DATE_FORMAT.format(new Date(validFrom));
    }

    public String getValidUntilAsString()
    {
        return SIMPLE_DATE_FORMAT.format(new Date(validUntil));
    }

}