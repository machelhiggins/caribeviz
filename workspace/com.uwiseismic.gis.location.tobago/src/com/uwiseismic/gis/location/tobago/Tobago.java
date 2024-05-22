package com.uwiseismic.gis.location.tobago;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Tobago extends Country
{
    public final static Tobago INSTANCE = new Tobago();
    public static final String ID = Tobago.class.getName();
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "21";
    public Tobago()
    {
        this.name = "Tobago";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
