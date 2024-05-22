package com.uwiseismic.gis.location.antigua;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Antigua extends Country
{
    public final static Antigua INSTANCE = new Antigua();
    public static final String ID = Antigua.class.getName();

    public Antigua()
    {
        this.name = "Antigua";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
