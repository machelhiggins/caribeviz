package com.uwiseismic.gis.location.jamaica;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Jamaica extends Country
{
    public final static Jamaica INSTANCE = new Jamaica();
    public static final String ID = Jamaica.class.getName();
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "8";

    public Jamaica(){
        this.name = "Jamaica";
    }

    public String getSubLocationType(){    	
        return City.TAG_SELF;
    }
}
