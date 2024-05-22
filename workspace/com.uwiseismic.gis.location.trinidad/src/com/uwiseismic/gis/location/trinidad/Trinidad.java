package com.uwiseismic.gis.location.trinidad;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Trinidad extends Country
{
    public final static Trinidad INSTANCE = new Trinidad();
    public static final String ID = Trinidad.class.getName();
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "20";

    public Trinidad()
    {
        this.name = "Trinidad";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
