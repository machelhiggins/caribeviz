package com.uwiseismic.gis.location.nevis;

import ncsa.gis.locations.Country;

public class Nevis extends Country
{
    public final static Nevis INSTANCE = new Nevis();
    public static final String ID = Nevis.class.getName();

    public Nevis()
    {
        this.name = "Nevis";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
