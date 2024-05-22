package com.uwiseismic.gis.location.stlucia;

import ncsa.gis.locations.Country;

public class Stlucia extends Country
{
    public final static Stlucia INSTANCE = new Stlucia();
    public static final String ID = Stlucia.class.getName();

    public Stlucia()
    {
        this.name = "Stlucia";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
