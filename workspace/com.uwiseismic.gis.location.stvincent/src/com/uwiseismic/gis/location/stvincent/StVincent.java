package com.uwiseismic.gis.location.stvincent;

import ncsa.gis.locations.Country;

public class StVincent extends Country
{
    public final static StVincent INSTANCE = new StVincent();
    public static final String ID = StVincent.class.getName();

    public StVincent()
    {
        this.name = "Stvincent";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
