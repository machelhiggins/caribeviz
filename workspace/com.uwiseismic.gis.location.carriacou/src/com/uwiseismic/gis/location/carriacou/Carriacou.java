package com.uwiseismic.gis.location.carriacou;

import ncsa.gis.locations.Country;

public class Carriacou extends Country
{
    public final static Carriacou INSTANCE = new Carriacou();
    public static final String ID = Carriacou.class.getName();

    public Carriacou()
    {
        this.name = "Carriacou";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
