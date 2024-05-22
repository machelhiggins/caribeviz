package com.uwiseismic.gis.location.grenada;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Grenada extends Country
{
    public final static Grenada INSTANCE = new Grenada();
    public static final String ID = Grenada.class.getName();
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "9";

    public Grenada()
    {
        this.name = "Grenada";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
