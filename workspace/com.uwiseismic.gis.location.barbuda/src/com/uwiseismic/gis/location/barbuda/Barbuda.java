package com.uwiseismic.gis.location.barbuda;

import ncsa.gis.locations.Country;

public class Barbuda extends Country
{
    public final static Barbuda INSTANCE = new Barbuda();
    public static final String ID = Barbuda.class.getName();

    public Barbuda()
    {
        this.name = "Barbuda";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
