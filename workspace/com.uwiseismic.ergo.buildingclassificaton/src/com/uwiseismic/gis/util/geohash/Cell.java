/*
 * Created on Jun 23, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.gis.util.geohash;

import java.util.ArrayList;
import java.util.Iterator;

import com.uwiseismic.gis.util.DegreeToMeter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class Cell{

    private Cell children[] = new Cell[4];
    private Geometry bb;
    private int level = 0;


    private ArrayList <GeoHashable>constituents = new ArrayList<GeoHashable>();

    public Cell(Geometry boundingBox, int level){
        this.bb = boundingBox;
        this.level = level;
        if(level > 0)
            generateChildrenCells();
    }

    private void generateChildrenCells(){
        Coordinate coords[] = bb.getCoordinates();
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -1*Double.MAX_VALUE;
        double maxY = -1*Double.MAX_VALUE;
        for(int i = 0; i < coords.length; i++){
            if(minX > coords[i].x)
                minX = coords[i].x;
            if(maxX < coords[i].x)
                maxX = coords[i].x;
            if(minY > coords[i].y)
                minY = coords[i].y;
            if(maxY < coords[i].y)
                maxY = coords[i].y;
        }

        double xHLen =  (maxX - minX)/2;
        double yHLen = (maxY - minY)/2;

        GeometryFactory fact = new GeometryFactory();
        //** 1s quadrant
        children[0] = new Cell(new Polygon(fact.createLinearRing(
                    new Coordinate[]{
                            new Coordinate(minX,maxY),
                            new Coordinate(minX + xHLen, maxY),
                            new Coordinate(minX + xHLen, minY + yHLen),
                            new Coordinate(minX, minY + yHLen),
                            new Coordinate(minX, maxY)
                    }
                ),
                null, fact), level - 1);

        //** 2nd quadrant
        children[1] = new Cell(new Polygon(fact.createLinearRing(
                    new Coordinate[]{
                            new Coordinate(minX + xHLen, maxY),
                            new Coordinate(maxX, maxY),
                            new Coordinate(maxX, minY + yHLen),
                            new Coordinate(minX + xHLen, minY + yHLen),
                            new Coordinate(minX + xHLen, maxY)
                    }
                ),
                null, fact), level - 1);

        //** 3rd quadrant
        children[2] = new Cell(new Polygon(fact.createLinearRing(
                    new Coordinate[]{
                            new Coordinate(minX + xHLen, minY + yHLen),
                            new Coordinate(maxX, minY + yHLen),
                            new Coordinate(maxX, minY),
                            new Coordinate(minX + xHLen, minY),
                            new Coordinate(minX + xHLen, minY + yHLen)
                    }
                ),
                null, fact), level - 1);

        //**4th quadrant
        children[3] = new Cell(new Polygon(fact.createLinearRing(
                    new Coordinate[]{
                            new Coordinate(minX + xHLen, minY + yHLen),
                            new Coordinate(minX + xHLen, minY),
                            new Coordinate(minX, minY),
                            new Coordinate(minX, minY + yHLen),
                            new Coordinate(minX + xHLen, minY + yHLen)
                    }
                ),
                null, fact), level - 1);
    }

    public Cell[] getChildrenCells(){
        return children;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the constituents
     */
    public ArrayList<GeoHashable> getConstituents() {
        return constituents;
    }

    /**
     * @param constituents the constituents to set
     */
    public boolean addConstituent(GeoHashable member) {

        if(bb.covers(member.getGeometry())//){
                || bb.intersects(member.getGeometry())
                	||member.getGeometry().covers(bb)
                		||member.getGeometry().intersects(bb)
                			||member.getGeometry().overlaps(bb)){
            if(level == 0){
                if(constituents == null)
                    constituents = new ArrayList<GeoHashable>();
                constituents.add(member);                
                return true;
            }
            else{
            	int i=0;

            	for(i =0; i < children.length; i++){
            		if(children[i] == null){
            			//System.out.println("child is null at level "+level);
            		}else
            			children[i].addConstituent(member);
            	}
            	if(i == children.length)return true;
            	return false;
            }
        }
        else{
//        	if(level == 5)
//        		System.err.println("Not adding");
        	return false;
        }
    }

//    public boolean addConstituentDEBUG(GeoHashable member) {
//
//        if(bb.covers(member.getGeometry())//){
//                || bb.intersects(member.getGeometry()) ||
//                member.getGeometry().covers(bb)){
//
//            if(level == 0){
//                if(constituents == null){
//                    constituents = new ArrayList<GeoHashable>();
//                }
//                System.err.println("[DEBUG] Cell::: Added member");
//                constituents.add(member);
//                return true;
//            }
//            else{
//            	for(int i =0; i < children.length; i++){
//            		if(children[i].addConstituentDEBUG(member)){
//            			System.err.println("[DEBUG] Cell::: Child found it at level "+level);
//            			return true;
//            		}
//            	}
//            	//** shouldnt reach here
//            	System.err.println("[DEBUG] Cell::: Cell covers member but children do not ("+level+")!!!!!!!!!!!!!");
//            	return true;
//            }
//        }
//        else{
//        	System.err.println("[DEBUG] Cell::: Straight up doesn't belong from "+level);
//        	return false;
//        }
//    }

//    public ArrayList <GeoHashable> getAnythingContainedIn(Geometry geom){
//    	ArrayList <GeoHashable> subset = new ArrayList <GeoHashable>();
//    	if(bb.covers(geom)){
//                //|| bb.intersects(geom)){
//    		if(level == 0){
//    			for(Iterator <GeoHashable>i = constituents.iterator(); i.hasNext();){
//    				GeoHashable t = i.next();
//    				if(geom.covers(t.getGeometry())){
//    					subset.add(t);
//    				}
//    			}
//    		}
//    		else{
//	    		for(int i =0; i < children.length; i++){
//	    			ArrayList <GeoHashable> t = children[i].getAnythingContainedIn(geom);
//	    			if(t != null){
//	    				subset.addAll(t);
//	    			}
//
//	        	}
//    		}
//    		return subset;
//    	}
//    	else {
//    		return null;
//    	}
//    }
    
    public ArrayList <GeoHashable> getAnythingContainedIn(Geometry geom){
    	ArrayList <GeoHashable> subset = new ArrayList <GeoHashable>();
    	if(bb.covers(geom)
    			|| geom.covers(bb)
    			|| geom.intersects(bb)){
                //|| bb.intersects(geom)){
    		if(level == 0){
    			for(Iterator <GeoHashable>i = constituents.iterator(); i.hasNext();){
    				GeoHashable t = i.next();
    				if(geom.covers(t.getGeometry())){
    					subset.add(t);
    				}
    			}
    		}
    		else{
	    		for(int i =0; i < children.length; i++){
	    			ArrayList <GeoHashable> t = children[i].getAnythingContainedIn(geom);
	    			if(t != null){
	    				subset.addAll(t);
	    			}

	        	}
    		}
    		return subset;
    	}
    	else {
    		return null;
    	}
    }

    public ArrayList <GeoHashable> getAnythingContaining(Geometry geom){
    	ArrayList <GeoHashable> subset = new ArrayList <GeoHashable>();
    	if(geom.covers(bb)
            || bb.covers(geom)
            	||bb.intersects(geom)){
    		if(level == 0){

    			for(Iterator <GeoHashable>i = constituents.iterator(); i.hasNext();){
    				GeoHashable t = i.next();
    				//added or
    				if(t.getGeometry().covers(geom) || geom.covers(t.getGeometry())
    						|| t.getGeometry().intersects(geom)
    						|| geom.intersects(t.getGeometry())
    						|| geom.within(t.getGeometry())
    						|| t.getGeometry().within(geom)
    						|| t.getGeometry().intersects(geom)
    						|| geom.intersects(t.getGeometry())
    						|| geom.overlaps(t.getGeometry())
    						|| t.getGeometry().overlaps(geom)
    						|| t.getGeometry().covers(geom)
    						|| geom.covers(t.getGeometry())
    						|| t.getGeometry().coveredBy(geom)
    						|| geom.coveredBy(t.getGeometry())){
    					subset.add(t);
    				}

    			}
    		}
    		else{
	    		for(int i =0; i < children.length; i++){
	    			ArrayList <GeoHashable> t = children[i].getAnythingContaining(geom);
	    			if(t != null){
	    				subset.addAll(t);
	    			}
	        	}
    		}
    		return subset;
    	}
    	else {
    		return null;
    	}
    }

    public ArrayList <GeoHashable> getAnythingIntersectedBy(Geometry geom){
    	ArrayList <GeoHashable> subset = new ArrayList <GeoHashable>();    	
    	if(bb.covers(geom)
                || bb.intersects(geom)){
    		if(level == 0){
    			for(Iterator <GeoHashable>i = constituents.iterator(); i.hasNext();){
    				GeoHashable t = i.next();
    				if(geom.intersects(t.getGeometry()))
    					subset.add(t);
    			}
    		}
    		else{
	    		for(int i =0; i < children.length; i++){
	    			ArrayList <GeoHashable> t = children[i].getAnythingIntersectedBy(geom);
	    			if(t != null)
	    				subset.addAll(t);
	        	}
    		}
    		return subset;
    	}
    	else
    		return null;
    }
    
    
    public Geometry getBoundingBox(){
        return bb;
    }


    public ArrayList <GeoHashable> getContentsOfBottomCell(Geometry geom){
        ArrayList <GeoHashable> subset = new ArrayList <GeoHashable>();
//        System.out.println("DEBUG:"+this.getClass().getName()+" cell (level = "+level+" BB = ["
//        +DegreeToMeter.degreeToMeter(bb.getEnvelopeInternal().getMaxX() ,bb.getEnvelopeInternal().getWidth())
//        +","
//        +DegreeToMeter.degreeToMeter(bb.getEnvelopeInternal().getMaxY() ,bb.getEnvelopeInternal().getHeight())
//        +"\t geom we're after= ["
//        +DegreeToMeter.degreeToMeter(geom.getEnvelopeInternal().getMaxX() ,geom.getEnvelopeInternal().getWidth())
//        +", "
//        +DegreeToMeter.degreeToMeter(geom.getEnvelopeInternal().getMaxY() ,geom.getEnvelopeInternal().getHeight())
//        +"]");
        if(bb.covers(geom)){
            if(level == 0){
                subset.addAll(constituents);
            }
            else{
                for(int i =0; i < children.length; i++){
                	if(children[i] != null){
	                    ArrayList <GeoHashable> t = children[i].getContentsOfBottomCell(geom);
	                    if(t != null){
	                        subset.addAll(t);
	                    }
                	}
                }
            }
            return subset;
        }
        else {
            return null;
        }
    }


}
