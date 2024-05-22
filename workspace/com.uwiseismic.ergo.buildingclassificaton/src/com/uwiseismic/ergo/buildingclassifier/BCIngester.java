package com.uwiseismic.ergo.buildingclassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;

import com.uwiseismic.ergo.roadnetwork.FeatureToMajorRoad;
import com.uwiseismic.gis.util.DegreeToMeter;
import com.uwiseismic.gis.util.ObjectToReal;
import com.uwiseismic.gis.util.geohash.GeoHash;
import com.uwiseismic.gis.util.geohash.GeoHashable;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 *
 * This class preprocesses FeatureCollections for census tracts w (census data and unimproved land value) and building footprints .
 * <br>The census tracts should have the following attributes per enumeration district:<br>
 * <ul>
 * <li> name - Enumeration District code </li>
 * <li> pop - total population</li>
 * <li> male - total males in population</li>
 * <li> female - total females in population</li>
 * <li> under_16 - total population under the age of 16</li>
 * <li> uil - the mean unimproved land value of plots in the ED </li>
 * <li> std_uil - the standard deviation of the unimproved land value of plots in the ED</li>
 * <li> area_sqm - area in meters squared</li>
 * </ul>
 *  <br>The building foot prints should have the Egro Building Inventory v4 attributes but
 *  it MUST have the following attributes<br>
 * <ul>
 * <li>bldg_id - building ID</li>
 * <li>struct_typ - structure type, can be null</li>
 * <li>occ_type -  occupancy type, can be null</li>
 * <li>str_prob - probability structure type is correct (0 - 1)</li>
 * <li>sq_foot AND\OR area_sqm - ground floor square footage\meters</li>
 * <li> u_set_occt - did user set the occupancy type</li>
 * <li>occ_nite_m - male occupancy rate for the night</li>
 * <li>occ_nite_f - female occupancy rate for the night</li>
 * <li>occ_nite_c - child occupancy rate for the night</li>
 * <li>occ_day_m - male occupancy rate for the day</li>
 * <li>occ_day_f -female occupancy rate for the day</li>
 * <li>occ_day_c children occupancy rate for the day</li>
 * <li>occ_avg_m - male average occupancy rate</li>
 * <li>occ_avg_f - female average occupancy rate</li>
 * <li>occ_avg_c - children average occupancy rate</li>
 * <li>u_set_occr - boolean: did user set the occupancy rayes</li>
 *
 * </ul>
 * <br>
 * Builing Inventory v4 attributes
           <li>the_geom</li>
           <li>parid</li>
           <li>struct_typ</li>
           <li>year_built</li>
           <li>no_stories</li>
           <li>occ_type</li>
           <li>appr_bldg</li>
           <li>cont_val</li>
           <li>efacility</li>
           <li>dwell_unit</li>
           <li>sq_foot</li>
           <li>str_typ2</li>

 * <br>
 * <br>The returning FeatuerCollection has the following attributes<br>
 * <ul>
 * <li> ed_name - Enumeration District code </li>
 * <li> pop - total population </li>
 * <li> male - total males in population</li>
 * <li> male_u16 - total males in population under age 16</li>
 * <li> female - total females in population</li>
 * <li> female_u16 - total females in population under age 16</li>
 * <li> pop_area - pop / area </li>
 * <li>pop_bldg_cnt - pop / building count</li>
 *  <li>area_bldg_cnt - area / building count</li>
 *  <li>bldg_count - building count</li>
 *  <li>bldg_mgsqft - mean ground square feet of buildings in ED</li>
 *  <li>b_stdgsqft - standard deviation of ground square feet of buildings in ED</li>
 *  <li>uil -  the mean unimproved land value of plots in the ED  </li>
 *  <li>std_uil - the standard deviation of the unimproved land value of plots in the ED</li>
 *  <li>area_sqm - area in meters squared</li>
 *  <li></li>
 *  </ul>
 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
 *
 */
public class BCIngester {

	private GeoHash preprocessedEDGH;
	private GeoHash bfptsGH;
	private int additionalBfptAttributes = 3; //** building characteristics and occupancy type and rate
	private boolean buildingsUsingSQM = false;
	private final org.apache.log4j.Logger logger =
			 org.apache.log4j.Logger.getLogger( this.getClass() );


	public void process(FeatureCollection eds, FeatureCollection bfpts, FeatureCollection OSMRoads,
			BuildingProbParameters probParams, IProgressMonitor monitor, int totalWorkUnits)throws Exception {

		/** Process Enumeration districts and create GeoHashables
	        */
		eds = DataUtilities.collection(eds);
		bfpts = DataUtilities.collection(bfpts);
		OSMRoads = DataUtilities.collection(OSMRoads);


		DefaultFeatureCollection preprocessed = new DefaultFeatureCollection();


		//** feature type string for preprocessed features
		StringBuffer featureTypeString = new StringBuffer();
        featureTypeString.append("ed_name:String");
        featureTypeString.append(",");
        featureTypeString.append("pop:Integer");//** because it will be normalized later
        featureTypeString.append(",");
        featureTypeString.append("male:Integer");//** will not be normalized
        featureTypeString.append(",");
        featureTypeString.append("female:Integer");//** wil not be normalized
        featureTypeString.append(",");
        featureTypeString.append("area_sqm:Double");
        featureTypeString.append(",");
        featureTypeString.append("pop_area:Double");
        featureTypeString.append(",");
        featureTypeString.append("pop_bldg_cnt:Double");
        featureTypeString.append(",");
        featureTypeString.append("area_bldg_cnt:Double");
        featureTypeString.append(",");
        featureTypeString.append("bldg_count:Integer");
        featureTypeString.append(",");
        featureTypeString.append("bldg_mgsqft:Double");
        featureTypeString.append(",");
        featureTypeString.append("bldg_stdgsqft:Double");
        featureTypeString.append(",");
        featureTypeString.append("uil:Double");
        featureTypeString.append(",");
        featureTypeString.append("std_uil:Double");
        featureTypeString.append(",");
        featureTypeString.append("male_u16:Integer");//** will not be normalized
        featureTypeString.append(",");
        featureTypeString.append("female_u16:Integer");//** wil not be normalized

        //** feature type string or building footprints

        double maxPop = -1*Double.MAX_VALUE;
        double maxArea = -1*Double.MAX_VALUE;
        double maxUIL = -1*Double.MAX_VALUE;
        double maxSTDEV_UIL = -1*Double.MAX_VALUE;
        double uilVal = 0;
        double  stdUilVal = 0;

        //** create Geohash for faster searching copy required attributes from eds  
        preprocessedEDGH = new GeoHash(GeoHash.getASensibleSmallestCellSize(eds.getBounds()), eds.getBounds());
        ArrayList <GeoHashable>t = new ArrayList<GeoHashable>();
        boolean geomSet =false;
        String edID;
        SimpleFeatureBuilder builder = null;
        double edsX[] = new double[eds.size()];//** x coord for ED centroid to be used later for assign to EDs to buuldings outside them
        double edsY[] = new double[eds.size()];//** y coord for ED centroid to be used later for assign to EDs to buuldings outside them
        EDStructTypeRankingFunction edArr[] = new EDStructTypeRankingFunction[eds.size()];
        int n = 0;
        for(FeatureIterator i = eds.features(); i.hasNext();){
        	SimpleFeature feat = (SimpleFeature)i.next();
        	//** create preprocessed features
        	Object attribs[] = new Object[16];
        	attribs[0] = feat.getDefaultGeometryProperty();
        	attribs[1] = feat.getAttribute("ed_name");
        	edID = (String)attribs[1];

        	Integer r = ObjectToReal.getMeInteger(feat.getAttribute("pop"));
        	attribs[2] = r;
        	if(maxPop < r.intValue())
        			maxPop = r.intValue();
        	r = ObjectToReal.getMeInteger(feat.getAttribute("male"));
        	attribs[3] = r;
        	r = ObjectToReal.getMeInteger(feat.getAttribute("female"));
        	attribs[4] = r;
        	Double rd = ObjectToReal.getMeDouble(feat.getAttribute("area_sqm"));
    		attribs[5] = rd;
    		if(maxArea < rd.doubleValue())
    			maxArea = rd.doubleValue();
    		rd = ObjectToReal.getMeDouble(feat.getAttribute("uil"));
    		attribs[12] = rd;
    		if(maxUIL < rd.doubleValue())
    			maxUIL = rd.doubleValue();

    		uilVal = rd.doubleValue();

    		rd = ObjectToReal.getMeDouble(feat.getAttribute("std_uil"));
    		attribs[13] = rd;
    		if(maxSTDEV_UIL < rd.doubleValue())
    			maxSTDEV_UIL = rd.doubleValue();


        	attribs[14] = ObjectToReal.getMeInteger(feat.getAttribute("male_u16"));;
        	attribs[15] = ObjectToReal.getMeInteger(feat.getAttribute("female_u16"));

    		if(!geomSet){
	    		if(((Geometry)feat.getDefaultGeometry())instanceof Polygon)
	    			featureTypeString = new StringBuffer("geom:Polygon,"+featureTypeString.toString());
	    		else
	    			featureTypeString = new StringBuffer("geom:MultiPolygon,"+featureTypeString.toString());
	    		geomSet = true;
	    	}

    		if(builder == null){
	            SimpleFeatureType featureType = null;
	            try {
	                featureType = DataUtilities.createType("uwiEnumerationDistrict",
	                        featureTypeString.toString());
	            } catch (SchemaException e1) {
	                e1.printStackTrace();
	                throw e1;
	            }
	            builder = new SimpleFeatureBuilder(featureType);
    		}
            builder.addAll(attribs);
            SimpleFeature newFeat = builder.buildFeature(null);
            newFeat.setDefaultGeometryProperty(feat.getDefaultGeometryProperty());

            preprocessed.add(newFeat);

            EDStructTypeRankingFunction ed = new EDStructTypeRankingFunction();
            ed.setFeature(newFeat);
            ed.setID(edID);
            ed.setMeanUnimpLandVal(uilVal);
            ed.setStdUnimpLandVal(stdUilVal);
            t.add(ed);
            
            // ** ED centroid
            edsX[n] = (feat.getBounds().getMaxX()+feat.getBounds().getMinX())/2;
            edsY[n] = (feat.getBounds().getMaxY()+feat.getBounds().getMinY())/2;
            edArr[n] = ed;
            n++;

        }
        preprocessedEDGH.setHashables((Collection<GeoHashable>)t);
        if(monitor != null)
        	monitor.worked((int)Math.floor((double)totalWorkUnits/5.0));

        /** Process Building footprints: create Geohash for building footprints; copy
        **and create necessary attributes
        */
        SimpleFeatureType featureType = null;
        featureTypeString = new StringBuffer(); //** Create feature type for a UWI Building Inventory data set
        //** Create FeaturetoMajorRoad object first.
        FeatureToMajorRoad roadMeasure = new FeatureToMajorRoad(OSMRoads);

        int bfptAttribsLength = 0;        
        bfptsGH = new GeoHash(GeoHash.getASensibleSmallestCellSize(bfpts.getBounds()), bfpts.getBounds());
        ArrayList<GeoHashable> bldgFootPrintsList = new ArrayList<GeoHashable>();
        for(FeatureIterator i = bfpts.features(); i.hasNext();){
        	SimpleFeature feat = (SimpleFeature)i.next();

        	SimpleFeature newFeat = this.createBCFeature(feat);

        	//** copy attributes

        	//** check ground floor area units and create sqm if not present
        	if(!buildingsUsingSQM || newFeat.getAttribute("area_sqm") == null){
        		newFeat.setAttribute("area_sqm", new Double(
        				ObjectToReal.getMeDouble(feat.getAttribute("sq_foot")).doubleValue() * 0.092903));
        	}


        	/*
        	 * Check if these attributes exists before setting them
				"n_vertices",
				"isop_q",
				"road_index",
        	 */
        	Geometry geom = ((Geometry)feat.getDefaultGeometry());
        	ReferencedEnvelope featRE = new ReferencedEnvelope(feat.getBounds());
        	//** do number of vertices measure
        	if(newFeat.getAttribute("n_vertices") == null){
	        	int noOfVertices = geom.getCoordinates().length -1; // minus 1 because of the last point to close loop
	        	newFeat.setAttribute("n_vertices" , new Integer(noOfVertices));
        	}

        	//** do isoperimetric quotient
        	if(newFeat.getAttribute("isop_q") == null){
	        	Point centroid = geom.getCentroid();
	        	double area = geom.getArea();

	        	double radius = Math.sqrt(Math.pow(centroid.getX() - featRE.getMaxX(), 2)
	        			+Math.pow(centroid.getY() - featRE.getMaxY(), 2));
	        	double isoperimetricQ = (4*Math.PI*area)/Math.pow((2*Math.PI*radius),2);
	        	newFeat.setAttribute("isop_q", new Double(isoperimetricQ));
        	}
        	//** do distance to major roads index
        	if(newFeat.getAttribute("road_index") == null){
	        	double majorRoadIndex = roadMeasure.measureFeatureToRoad(feat);
	        	/** Need to create and index of 0 to 1 for majorRoadIndex.
	        	 *  We won't consider any building beyond 300 meters from road. That is
	        	 *  major-road-index =  1 at distances >= 300 meters.
	        	 *
	        	 */
	        	majorRoadIndex = DegreeToMeter.degreeToMeter(featRE.getMaxY(), majorRoadIndex);
	        	if(majorRoadIndex > 200)
	        		majorRoadIndex = 1;
	        	else
	        		majorRoadIndex = majorRoadIndex/200;
	        	newFeat.setAttribute("road_index", new Double(majorRoadIndex));
        	}


        	newFeat.setDefaultGeometry(feat.getDefaultGeometry());
        	BuildingClassification buildingClassification = new BuildingClassification(newFeat);
        	buildingClassification.setAllBuildings(bfptsGH);
        	buildingClassification.setProbParams(probParams);
        	bldgFootPrintsList.add(buildingClassification);


        	//****DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG ***
        	//****DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG ***
/*        	double debugID = ObjectToReal.getMeDouble(feat.getAttribute("bldg_id"));
        	buildingClassification.setBldgID(""+debugID);
        	if(debugID == 32490.0 ||
        			debugID == 19133.0
        			|| debugID == 19194.0
        			|| debugID == 38958.0
        			|| debugID == 19131.0
        			|| debugID == 45585.0
        			|| debugID == 45984.0
        			|| debugID == 20027.0

        			){
        		buildingClassification.setBldgID(""+debugID);
        		//if(debugID == 19133.0)//** big structure
        		//if(debugID == 19131.0)//** big structure that should be C1
        		//if(debugID == 32490.0)//** small structure
        		//if(debugID == 38958)// this is a random RES structure surrounded by what must be residential structures
        		//if(debugID == 19194.0)//** island life building
        		if(debugID ==  20027.0){//** random
        			buildingClassification.DEBUG = true;
        		}
        	}*/
        	//****DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG ***
        	//****DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG *******DEBUG ***

        }
        bfptsGH.setHashables((Collection<GeoHashable>)bldgFootPrintsList);

        if(monitor != null)
        	monitor.worked((int)Math.floor((double)totalWorkUnits/5.0));
        

        /** Search for buildings in ED  AND do neccessary  calculations
         *
         */
         double maxPopArea = -1*Double.MAX_VALUE;
         double maxBldgCount = -1*Double.MAX_VALUE;
         double maxBldgMeanSqft = -1*Double.MAX_VALUE;
         double maxBldgSTDEVSqft = -1*Double.MAX_VALUE;
         double maxPopBldgCnt = -1*Double.MAX_VALUE;
         double maxAreaBldgCnt = -1*Double.MAX_VALUE;
         for(Iterator <EDStructTypeRankingFunction>i = preprocessedEDGH.getHashables().iterator(); i.hasNext();){
         	EDStructTypeRankingFunction ed = i.next();

        	SimpleFeature feat = ed.getFeature();

        	double pop = ObjectToReal.getMeInteger(feat.getAttribute("pop")).doubleValue();
        	double area_sqm = ((Double)feat.getAttribute("area_sqm")).doubleValue();
        	double pop_area =pop/area_sqm;
	        feat.setAttribute("pop_area", new Double(pop_area));
	        if(maxPopArea < pop_area)
	        	maxPopArea = pop_area;
        	double bldg_count = 0;
        	double mean_bldg_size = 0;
        	double stdgsqft = 0;
        	double pop_bldg_cnt = 0;
        	double area_bldg_cnt = 0;
        
        	double mean_sqm = 0;
        	double std_sqm = 0;
        	double min_sqm = Double.MAX_VALUE;
        	double max_sqm = -Double.MAX_VALUE;
        	
        	ArrayList <GeoHashable>bldgs = bfptsGH.getContaining((Geometry)feat.getDefaultGeometry());
        	if(bldgs != null){
        		bldg_count = bldgs.size();
	        	for(Iterator <GeoHashable>z = bldgs.iterator(); z.hasNext();){
	        		BuildingClassification bc = (BuildingClassification)z.next();
	        		bc.setED(ed);
	        		ed.addBuildings(bc);
	        		SimpleFeature b = bc.getFeature();
	        		
	        		mean_bldg_size += ObjectToReal.getMeDouble(b.getAttribute("sq_foot")).doubleValue();
	        		double sqm = ObjectToReal.getMeDouble(b.getAttribute("area_sqm")).doubleValue();
	        		mean_sqm += sqm; 
	        		if(min_sqm > sqm)
	        			min_sqm = sqm;
	        		if(max_sqm < sqm)
	        			max_sqm = sqm;
	        		
	        	}
	        	if(bldg_count > 0){
	        		mean_bldg_size /= bldg_count;
	        		mean_sqm/= bldg_count;
		        	for(Iterator <GeoHashable>z = bldgs.iterator(); z.hasNext();){
		        		SimpleFeature b = ((BuildingClassification)z.next()).getFeature();
		        		stdgsqft += Math.pow(mean_bldg_size - 
		        				ObjectToReal.getMeDouble(b.getAttribute("sq_foot")).doubleValue(), 2);
		        		std_sqm += Math.pow(mean_sqm - 
		        				ObjectToReal.getMeDouble(b.getAttribute("area_sqm")).doubleValue(), 2);
		        	}

		        	stdgsqft = Math.sqrt(stdgsqft/bldg_count);
		        	std_sqm = Math.sqrt(std_sqm/bldg_count);
		        	pop_bldg_cnt =pop/bldg_count;
		        	area_bldg_cnt = area_sqm/bldg_count;
	        	}

	        	if(maxBldgCount < bldg_count)
	        		maxBldgCount = bldg_count;
	        	if(maxBldgMeanSqft < mean_bldg_size)
	        		maxBldgMeanSqft = mean_bldg_size;
	        	if(maxBldgSTDEVSqft < stdgsqft)
	        		maxBldgSTDEVSqft = stdgsqft;
	        	if(maxPopBldgCnt < pop_bldg_cnt)
	        		maxPopBldgCnt = pop_bldg_cnt;
	        	if(maxAreaBldgCnt < area_bldg_cnt)
        		maxAreaBldgCnt = area_bldg_cnt;
        	}
        	ed.setMeanSQM(mean_sqm);
        	ed.setStdSQM(std_sqm);
        	ed.setSqmRange(max_sqm - min_sqm);
        	
        	ed.setPop(pop);
        	ed.setPopArea(pop_area);
        	//feat.setAttribute("bldg_count", new Integer((int)bldg_count));
        	ed.setBldgCount(bldg_count);
        	//feat.setAttribute("bldg_mgsqft", new Double(mean_bldg_size));
        	ed.setMeanGSQFT(mean_bldg_size);
        	ed.setNonNormalizedMeanGSQFT(mean_bldg_size);
        	//feat.setAttribute("bldg_stdgsqft", new Double(stdgsqft));
        	ed.setStdGSQFT(stdgsqft);
        	ed.setNonNormalizedStdGSQFT(stdgsqft);
        	//feat.setAttribute("pop_bldg_cnt", new Double(pop_bldg_cnt));
        	ed.setPopBldgCount(pop_bldg_cnt);
        	//feat.setAttribute("area_bldg_cnt", new Double(area_bldg_cnt));
        	ed.setAreaBldgCount(area_bldg_cnt);

        }
         // ** Test if buildings were not assigned to an ED due to inaccurate ED shapefile
         // **
         //edArr
         Point bldgCentroid;
         double minDistance = 0;
         double edBldgDist = 0;
         int ind = 0;
         for(Iterator i = bldgFootPrintsList.iterator(); i.hasNext();){
        	 BuildingClassification bldg = (BuildingClassification)i.next();
        	 if(bldg.getED() == null){
        		 logger.warn("Building was not found in an Enumeration District. Assigning bldg_id = "+bldg.getBldgID()+" to closest Enumeration District.");
        		 minDistance = Double.MAX_VALUE;
        		 bldgCentroid = bldg.getCentroid();
        		 for(int z = 0; z < edsX.length; z++){
        			 edBldgDist = Math.sqrt(Math.pow(bldgCentroid.getX()-edsX[z],2) 
        					 + Math.pow(bldgCentroid.getY()-edsY[z],2));
        			 if( minDistance > edBldgDist){
        				 minDistance = edBldgDist;
        				 ind = z;
        			 }
        		 }
        		 bldg.setED(edArr[ind]);
        	 }        	 
         }
         
         if(monitor != null)
         	monitor.worked((int)Math.floor((double)totalWorkUnits/5.0));
         
        //** Normalize all ED's values except male and female and upper percentile unimproved land values
         EDUILValue edsUILs[] = new EDUILValue[eds.size()];//** used for determining 90 percentile value
         n = 0;// ** index for edsUILs ^^^
         for(Iterator <EDStructTypeRankingFunction>i = preprocessedEDGH.getHashables().iterator(); i.hasNext();){
        	EDStructTypeRankingFunction ed = i.next();
        	double val = ed.getPop()/maxPop;
        	ed.setPop(val);
           	ed.getFeature().setAttribute("pop",new Double(val));

           	val = ed.getPopArea()/maxPopArea;
           	ed.setPopArea(val);
        	ed.getFeature().setAttribute("pop_area",new Double(val));

        	val = ed.getPopBldgCount()/maxPopBldgCnt;
        	ed.setPopBldgCount(val);
        	ed.getFeature().setAttribute("pop_bldg_cnt",new Double(val));

        	val = ed.getAreaBldgCount()/maxAreaBldgCnt;
        	ed.setAreaBldgCount(val);
        	ed.getFeature().setAttribute("area_bldg_cnt",new Double(val));

        	val = ed.getBldgCount()/maxBldgCount;
        	ed.setBldgCount(val);
        	ed.getFeature().setAttribute("bldg_count",new Double(val));

        	val = ed.getMeanGSQFT()/maxBldgMeanSqft;
        	ed.setMeanGSQFT(val);
        	ed.getFeature().setAttribute("bldg_mgsqft",new Double(val));

        	val = ed.getStdGSQFT()/maxBldgSTDEVSqft;
        	ed.setStdGSQFT(val);
        	ed.getFeature().setAttribute("bldg_stdgsqft",new Double(val));

        	val = ed.getMeanUnimpLandVal()/maxUIL;
        	ed.setMeanUnimpLandVal(val);
        	ed.getFeature().setAttribute("uil",new Double(val));

        	val = ed.getStdUnimpLandVal()/maxSTDEV_UIL;
        	ed.setStdUnimpLandVal(val);
        	ed.getFeature().setAttribute("std_uil",new Double(val));
            edsUILs[n] = new EDUILValue(ed.getMeanUnimpLandVal(), ed);
            n++;

        	ed.calculateScores();
        }
        /** determine upper 90% percentile value for unimproved land values
         *
         */
        Arrays.sort(edsUILs);
        double upper90UILVal = edsUILs[(int)(90.0*(double)edsUILs.length/100.0)].getUil();
        double upper80UILVal = edsUILs[(int)(80.0*(double)edsUILs.length/100.0)].getUil();
        double upper70UILVal = edsUILs[(int)(70.0*(double)edsUILs.length/100.0)].getUil();
        logger.debug("Enumeratio Districts UIL stats: upper90UILVal:"+upper90UILVal+" \tupper80UILVal: "+upper80UILVal+"\tupper70UILVal: "+upper70UILVal);
        for(Iterator <EDStructTypeRankingFunction>i = preprocessedEDGH.getHashables().iterator(); i.hasNext();){
        	EDStructTypeRankingFunction ed = i.next();
        	//** set upper percentiles for unimproved land value
         	ed.setUpper90PercentileUnimpLandVal(upper90UILVal);
         	ed.setUpper80PercentileUnimpLandVal(upper80UILVal);
         	ed.setUpper70PercentileUnimpLandVal(upper70UILVal);
        }

        //** histogram equalize structure scores for ED
        //new HistogramNormalizeEDStruct()
        //	.performHistogramEqualization(new ArrayList<EDStructTypeRankingFunction>(preprocessedEDGH.getHashables()));

        /** Calculate initial probability for strucutures, normalized ED structure rankings and
        * and set structure fractional count per ED
        * */
        int debugNotInED = 0;
        double structCount[] = new double[ProbConstants.STRUCT_TYPES.length];
        // will multithread this bit
        int cores = Runtime.getRuntime().availableProcessors() - 2;
        BuildingClassificationThread bcThreads[] = new BuildingClassificationThread[cores];
        int bcThreadsRunning = 0;
        int m = 0;
        boolean startedThread = false;
        int totalNumBuildings = bfptsGH.getHashables().size();
        int determineStuctIter = 0;
        int debugStarted = 0;
        int debugDone = 0;
        long cpuBusyWork = 0;
        int lastWorked = 0;
        int worked = 0;
        logger.debug("Multithreading ("+cores+" threads) initial building classification for "+totalNumBuildings+ " buildings");
        for(Iterator <BuildingClassification>i = bfptsGH.getHashables().iterator(); i.hasNext();){
        	BuildingClassification b = i.next();

        	// ** Do multithread
        	for(m = 0; m < bcThreads.length; m++ ){
        		if(bcThreads[m] == null){
        			bcThreads[m] = new BuildingClassificationThread(b);
        			bcThreads[m].start();
        			bcThreadsRunning++;
        			debugStarted++;
        			break;
        		}            		
        	}
        	//** check if we didn't have an open thread
        	if(m == bcThreads.length){
        		startedThread = false;
        		//** Check if any threads are done
        		while(!startedThread){
        			for(m = 0; m < bcThreads.length; m++ ){
        				if(bcThreads[m] != null && !bcThreads[m].isRunning()){        					       				
        					try {
        						bcThreads[m].wereThereExeptionsDuringRun();
        					}catch(StructureNotInEdException ignore){
	        	        		debugNotInED++;
	        	        		logger.warn("Found and IGNORING a building not in an Enumeration District: "+ignore.getMessage());
	        	        	}catch(NoProbabilityFunctionException oops){
	        	        		logger.warn("Found a building with no set structure probability function"+oops.getMessage()); 
	        	        	}
        					bcThreads[m] = null; 
        					debugDone++;
        					bcThreadsRunning--;    

        					if(!startedThread){
        						bcThreads[m] = new BuildingClassificationThread(b);
        	        			bcThreads[m].start();        	        			
        	        			bcThreadsRunning++;
        	        			startedThread = true;
        	        			debugStarted++;
        	        			
        					}
        				}
        			}        			
        			// **lets wait if no threads area available
        			if(!startedThread){            				
        				Thread.sleep(300);        				
        			}
        		}
        	}
        	determineStuctIter++;
        	if(determineStuctIter%100 == 0){
                if(monitor != null){                	
                	worked = (int)Math.floor((0.5*determineStuctIter/totalNumBuildings)*(double)totalWorkUnits/5.0);  
                	if((worked-lastWorked) >= 1)
                		monitor.worked(1);
                	lastWorked = worked;
                }
        	}
        }
        //** wait on threads running
        while(bcThreadsRunning > 0){
        	for(m = 0; m < bcThreads.length; m++ ){
				if(bcThreads[m] != null && !bcThreads[m].isRunning()){        					       				
					try {
						bcThreads[m].wereThereExeptionsDuringRun();
					}catch(StructureNotInEdException ignore){
    	        		debugNotInED++;
    	        		logger.warn("Found and IGNORING a building not in an Enumeration District: "+ignore.getMessage());
    	        	}catch(NoProbabilityFunctionException oops){
    	        		logger.warn("Found a building with no set structure probability function"+oops.getMessage()); 
    	        	}
					bcThreads[m] = null; 
					bcThreadsRunning--;	
				}
			}
			// **lets wait if no threads area available
			if(bcThreadsRunning > 0){        				
				Thread.sleep(300);				
			}
        }
        if(debugNotInED != 0)
        	logger.warn("Number of buildings not in ED: "+debugNotInED);//** DEBUG


        /** Recalculate structural scores for EDS
         *
         */
        //** determine max structure scores for use in normalization later
        double rmOneScoreMax= -Double.MAX_VALUE;
		double rmTwoScoreMax= -Double.MAX_VALUE;
		double w1ScoreMax= -Double.MAX_VALUE;
		double pcScoreMax= -Double.MAX_VALUE;
		double cOneScoreMax= -Double.MAX_VALUE;
		double cTwoScoreMax= -Double.MAX_VALUE;
		double cThreeScoreMax= -Double.MAX_VALUE;
		double urmScoreMax= -Double.MAX_VALUE;
		double sScoreMax= -Double.MAX_VALUE;
        for(Iterator <EDStructTypeRankingFunction>i = preprocessedEDGH.getHashables().iterator(); i.hasNext();){
        	EDStructTypeRankingFunction ed = i.next();
        	Collection buildings = ed.getBuildings();
        	double structInED[] =  new double[ProbConstants.STRUCT_TYPES.length];
        	for(Iterator <BuildingClassification> z = buildings.iterator();z.hasNext();){
        		BuildingClassification d = z.next();
        		structInED[d.getMostLikelyStructure()]++;
        	}
        	ed.setFractionalCOne(structInED[ProbConstants.C1]/structCount[ProbConstants.C1]);
        	ed.setFractionalCThree(structInED[ProbConstants.C2]/structCount[ProbConstants.C2]);
        	ed.setFractionalCTwo(structInED[ProbConstants.C2]/structCount[ProbConstants.C2]);
        	ed.setFractionalPCOne(structInED[ProbConstants.PC1]/structCount[ProbConstants.PC1]);
        	ed.setFractionalPCTwo(structInED[ProbConstants.PC1]/structCount[ProbConstants.PC1]);
        	ed.setFractionalRMOne(structInED[ProbConstants.RM1]/structCount[ProbConstants.RM1]);
        	ed.setFractionalRMTwo(structInED[ProbConstants.RM2]/structCount[ProbConstants.RM2]);
        	ed.setFractionalS(structInED[ProbConstants.S1]/structCount[ProbConstants.S1]);
        	ed.setFractionalURM(structInED[ProbConstants.URM]/structCount[ProbConstants.URM]);
        	ed.setFractionalWOne(structInED[ProbConstants.W1]/structCount[ProbConstants.W1]);
        	ed.calculateScores();

        	if( rmOneScoreMax < ed.getRmOneScore()){ rmOneScoreMax = ed.getRmOneScore(); }
        	if( rmTwoScoreMax < ed.getRmTwoScore()){ rmTwoScoreMax = ed.getRmTwoScore(); }
        	if( w1ScoreMax < ed.getW1Score()){ w1ScoreMax = ed.getW1Score(); }
        	if( pcScoreMax < ed.getPCScore()){ pcScoreMax = ed.getPCScore();}
        	if( cOneScoreMax < ed.getCOneScore()){ cOneScoreMax = ed.getCOneScore(); }
        	if( cTwoScoreMax < ed.getcTwoScore()){ cTwoScoreMax = ed.getcTwoScore(); }
        	if( cThreeScoreMax < ed.getcThreeScore()){ cThreeScoreMax = ed.getcThreeScore(); }
        	if( urmScoreMax < ed.getURMScore()){ urmScoreMax = ed.getURMScore(); }
        	if( sScoreMax < ed.getSScore()){ sScoreMax = ed.getSScore();}
        }

        //** set max scores for structure types for normalization purposes
        for(Iterator <EDStructTypeRankingFunction>i = preprocessedEDGH.getHashables().iterator(); i.hasNext();){
        	EDStructTypeRankingFunction ed = i.next();
        	ed.setPcScoreMax(pcScoreMax);
        	ed.setRmOneScoreMax(rmOneScoreMax);
        	ed.setRmTwoScoreMax(rmTwoScoreMax);
        	ed.setUrmScoreMax(urmScoreMax);
        	ed.setW1ScoreMax(w1ScoreMax);
        	ed.setcOneScoreMax(cOneScoreMax);
        	ed.setcThreeScoreMax(cThreeScoreMax);
        	ed.setcTwoScoreMax(cTwoScoreMax);
        	ed.setsScoreMax(sScoreMax);
        	StructureProbabilities sp = ed.getStructureScore();
        	//if(sp.c1 > 1 || sp.c2 > 1 || sp.pc1 > 1 || sp.rm > 1
        		//	|| sp.rm1 > 1 || sp.rm2 > 1 || sp.s1 > 1 || sp.w1 > 1)
        		//System.out.println(ed.getStructureScore());//*** DEBUG**************************************
        }

        logger.debug("Multithreading ("+cores+" threads) post-ED-characterization building classification for "+totalNumBuildings+ " buildings");
        /** Recalculate probabilities of buildings since ED's structure rankings were recalculated
         *
         */
        for(Iterator <BuildingClassification>i = bfptsGH.getHashables().iterator(); i.hasNext();){
        	BuildingClassification b = i.next();
//        	try{
//        		b.determineStructure(true, true);
//        		//System.out.println(b.getBldgID()+"\t"+b.getMostLikelyStructureString());
//
//        	}catch(StructureNotInEdException ignore){
//        		//ignore.printStackTrace();
//        		System.out.println("Found a building not in an ED "+ignore.getMessage());
//        	}catch(NoProbabilityFunctionException oops){
//        		oops.printStackTrace();
//        		//TODO: Proper error reporting !!!!!!!!
//        	}
        	// ** Do multithread instead
        	for(m = 0; m < bcThreads.length; m++ ){
        		if(bcThreads[m] == null){
        			bcThreads[m] = new BuildingClassificationThread(b);
        			bcThreads[m].start();
        			bcThreadsRunning++;
        			break;
        		}            		
        	}
        	//** check if we didn't have an open thread
        	if(m == bcThreads.length){
        		startedThread = false;
        		//** lets check if any threads are done
           		while(!startedThread){
        			for(m = 0; m < bcThreads.length; m++ ){
        				if(bcThreads[m] != null && !bcThreads[m].isRunning()){        					       				
        					try {
        						bcThreads[m].wereThereExeptionsDuringRun();
        					}catch(StructureNotInEdException ignore){
	        	        		debugNotInED++;
	        	        		logger.warn("Found and IGNORING a building not in an Enumeration District: "+ignore.getMessage());
	        	        	}catch(NoProbabilityFunctionException oops){
	        	        		logger.warn("Found a building with no set structure probability function"+oops.getMessage()); 
	        	        	}
        					bcThreads[m] = null; 
        					bcThreadsRunning--;
        					if(!startedThread){
        						bcThreads[m] = new BuildingClassificationThread(b);
        	        			bcThreads[m].start();
        	        			bcThreadsRunning++;
        	        			startedThread = true;
        					}
        				}
        			}
        			// **lets wait if no threads area available
        			if(!startedThread){    
        				Thread.sleep(200);        				
        			}
        		}
        	}
        	determineStuctIter++;
        	if(determineStuctIter%100 == 0){
        		if(monitor != null){                	
                	worked = (int)Math.floor((0.5*determineStuctIter/totalNumBuildings)*(double)totalWorkUnits/5.0);  
                	if((worked-lastWorked) >= 1)
                		monitor.worked(1);
                	lastWorked = worked;
                }
        	}
        }
      //** wait on threads running
        while(bcThreadsRunning > 0){
        	for(m = 0; m < bcThreads.length; m++ ){
				if(bcThreads[m] != null && !bcThreads[m].isRunning()){        					       				
					try {
						bcThreads[m].wereThereExeptionsDuringRun();
					}catch(StructureNotInEdException ignore){
    	        		debugNotInED++;
    	        		logger.warn("Found and IGNORING a building not in an Enumeration District: "+ignore.getMessage());
    	        	}catch(NoProbabilityFunctionException oops){
    	        		logger.warn("Found a building with no set structure probability function"+oops.getMessage()); 
    	        	}
					bcThreads[m] = null; 
					bcThreadsRunning--;	
				}
			}
			// **lets wait if no threads area available
			if(bcThreadsRunning > 0){        				
				Thread.sleep(300);
			}
        }
        
        if(monitor != null)
        	monitor.worked((int)Math.floor((double)totalWorkUnits/5.0));
	}

	public GeoHash getBuildings(){
		return bfptsGH;
	}

	public GeoHash getEdRankings(){
		return preprocessedEDGH;
	}

	private SimpleFeature createBCFeature(SimpleFeature feat) throws SchemaException{
		StringBuffer featType = new StringBuffer();

		//** copy attribute types
		AttributeType att[] = new AttributeType[feat.getAttributeCount()];
    	feat.getFeatureType().getTypes().toArray(att);
    	Object newAttribs[] = new Object[buildingClassificationAttribs.length+1];

    	String geom = "";
    	boolean ef_occ_ratInt = false;
    	boolean max_occInt = false;
    	for(int z = 0;  z < att.length; z++){
    		if(att[z].getName().getLocalPart().matches("Polygon")
    				|| att[z].getName().getLocalPart().matches("MultiPolygon")){
    			geom = "the_geom:"+att[z].getBinding().getName();
    			newAttribs[0] = att[z];
    		}    	
   
    		else if(att[z].getBinding().getName().matches("ef_occ_rat")
    				&& feat.getAttribute(z) instanceof Integer)
    			ef_occ_ratInt = true;    		
    		else if(att[z].getBinding().getName().matches("max_occ")
    				&& feat.getAttribute(z) instanceof Integer)
    			max_occInt = true;    		
    		if(att[z].getName().getLocalPart().equals("area_sqm"))
    			buildingsUsingSQM = true;
    	}

    	featType.append(geom);
    	featType.append(",");
    	for(int n = 0; n < buildingClassificationAttribs.length; n++){
    		featType.append(buildingClassificationAttribs[n]);
    		featType.append(":");
    		featType.append(buildingClassificationAttribTypes[n]);
    		if( n+1 < buildingClassificationAttribs.length)
    			featType.append(",");
    		if(ef_occ_ratInt &&
    				buildingClassificationAttribTypes[n].matches("ef_occ_rat")){
    			newAttribs[n+1] = ObjectToReal.getMeDouble(feat.getAttribute(buildingClassificationAttribs[n]));
    		}else if(max_occInt &&
    				buildingClassificationAttribTypes[n].matches("")){
    			newAttribs[n+1] = ObjectToReal.getMeDouble(feat.getAttribute(buildingClassificationAttribs[n]));
    		}else
    			newAttribs[n+1] = feat.getAttribute(buildingClassificationAttribs[n]);
    	}

    	SimpleFeatureType featureType = DataUtilities.createType("uwiBuildingInventory",
        		featType.toString());
    	SimpleFeature newFeat = SimpleFeatureBuilder.build( featureType, newAttribs, "fid" ); //** this way seems to be buggy
    	
    	if(newFeat.getAttribute("guid") == null)
    		newFeat.setAttribute("guid", ""+new Random(System.currentTimeMillis()).nextDouble()*System.currentTimeMillis());
    		

		return newFeat;
	}

	private String buildingClassificationAttribs[] = {
			"guid",
			"parid",
			"bldg_id",
			"struct_typ",
			"year_built",
			"no_stories",
			"occ_type",
			"appr_bldg",
			"cost_sqft",
			"cont_val",
			"efacility",
			"dwell_unit",
			"sq_foot",
			"str_typ2",
			"str_prob",
			"efacil_nam",
			"ef_occ_rat",
			"max_occ",
			"gsq_feet",
			"area_sqm",
			"u_set_occt",
			"occ_nite_m",
			"occ_nite_f",
			"occ_nite_c",
			"occ_day_m",
			"occ_day_f",
			"occ_day_c",
			"occ_avg_m",
			"occ_avg_f",
			"occ_avg_c",
			"u_set_occr",
			"n_vertices",
			"isop_q",
			"road_index"
			};

	private String buildingClassificationAttribTypes[] = {
			"java.lang.String",
			"java.lang.String",
			"java.lang.String",
			"java.lang.String",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.String",
			"java.lang.Double",
			"java.lang.Double",
			"java.lang.Double",
			"java.lang.String",
			"java.lang.Integer",
			"java.lang.Double",
			"java.lang.String",
			"java.lang.Double",
			"java.lang.String",
			"java.lang.Double",
			"java.lang.Double",
			"java.lang.Double",
			"java.lang.Double",
			"java.lang.String",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.Integer",
			"java.lang.String",
			"java.lang.Integer",
			"java.lang.Double",
			"java.lang.Double"};


	/**
	 * Lazy wrapper to hold ED and Unimoproved land value tuple for comparison, sort etc
	 *
	 * @author Machel Higgins <mailto: machelhiggins@hotmail.com>
	 *
	 */
	class EDUILValue implements Comparable{

		private double uil = 0;
		private EDStructTypeRankingFunction ed;

		public EDUILValue(double uil, EDStructTypeRankingFunction ed){
			this.uil = uil;
			this.ed = ed;
		}


		public double getUil() {
			return uil;
		}

		public EDStructTypeRankingFunction getEd() {
			return ed;
		}

		public int compareTo(Object o) {
			return Double.compare(uil, ((EDUILValue)o).getUil());
		}

	}


}
