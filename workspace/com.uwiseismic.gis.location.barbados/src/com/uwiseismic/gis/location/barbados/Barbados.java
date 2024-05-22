package com.uwiseismic.gis.location.barbados;

import edu.illinois.ncsa.ergo.gis.locations.Country;

public class Barbados extends Country
{
    public final static Barbados INSTANCE = new Barbados();
    public static final String ID = Barbados.class.getName();
	public final static String TAG_REGION_CODE = "region_code"; //$NON-NLS-1$
	public final static String REGION_ID = "3";

    public Barbados()
    {
        this.name = "Barbados";
    }

    public String getSubLocationType()
    {
        return City.TAG_SELF;
    }

}
