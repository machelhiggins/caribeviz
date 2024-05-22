package com.uwiseismic.gis.util;

public class ObjectToReal {

	
	public static Double getMeDouble(Object obj){
		Double t;
		if(obj == null)
			return new Double(0);

		try{
			t = (Double)obj;
		}catch (ClassCastException ccex){
			try{
				t = new Double(((Integer)obj).doubleValue());
			}catch (ClassCastException ccex2){
				try{
					t = new Double(((Long)obj).doubleValue());
				}catch (ClassCastException ccex3){
					try{
						t = new Double(Double.parseDouble((String)obj));
					}catch (ClassCastException ccex4){
						//return null;
						return new Double(0);
					}
				}
			}
		}
		if(Double.isNaN(t))
			return new Double(0);
		return t;
	}
	
	public static Integer getMeInteger(Object obj){
		Integer t;
		if(obj == null)
			return new Integer(0);
		try{
			t = (Integer)obj;
		}catch (ClassCastException ccex){
			try{
				t = new Integer(((Double)obj).intValue());
			}catch (ClassCastException ccex2){
				try{
					t = new Integer(((Long)obj).intValue());
				}catch (ClassCastException ccex3){
					try{
						t = new Integer(Integer.parseInt((String)obj));
					}catch (ClassCastException ccex4){
						//return null;
						return new Integer(0);
					}
				}
			}
		}
		if(Double.isNaN(t))
			return new Integer(0);
		return t;
	}
	
	public static Long getMeLong(Object obj){
		Long t;
		if(obj == null)
			return new Long(0);
		try{
			t = (Long)obj;
		}catch (ClassCastException ccex){
			try{
				t = new Long(((Double)obj).longValue());
			}catch (ClassCastException ccex2){
				try{
					t = new Long(((Integer)obj).longValue());
				}catch (ClassCastException ccex3){
					try{
						t = new Long(Long.parseLong((String)obj));
					}catch (ClassCastException ccex4){
						//return null;
						return new Long(0);
					}
				}
			}
		}
		if(Double.isNaN(t))
			return new Long(0);
		return t;
	}
	
	public static Boolean getMeBoolean(Object obj){
		Boolean t;
		if(obj == null)
			return new Boolean(false);
		try{
			t = (Boolean)obj;
		}catch (ClassCastException ccex){
			try{
				t = new Boolean((String) obj);
			}catch (ClassCastException ccex2){
				return new Boolean(false);
			}
		}
		return t;
	}
}
