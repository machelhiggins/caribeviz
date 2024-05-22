package com.uwiseismic.gis.location.dominica;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Dominica extends Country
{
    public final static Dominica INSTANCE = new Dominica();
    public static final String ID = Dominica.class.getName();

    public Dominica()
    {
        this.name = "Dominica";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
