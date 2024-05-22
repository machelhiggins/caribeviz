/*
 * Created on Sep 28, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import com.hexiong.jdbf.DBFReader;
//import com.hexiong.jdbf.JDBFException;

public class BuildingAreaPerED {

    public static void main(String[] args) {
//        HashMap <String, BuildingAreaPerED> EDBldngCount = new HashMap <String, BuildingAreaPerED>();
//        String EDCode = "EDCode";
//        String pop = "TOTPOP";
//
//        String id= null;
//
//        String dbfFile = "J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\Caribeviz Building Inventory\\v1.4\\Kingston_20120717_SRCUWI_v1.4.2.dbf";
//        BuildingAreaPerED bed;
//        try {
//            DBFReader dbfreader = new DBFReader(dbfFile);
//
//
//            for(int i = 0; dbfreader.hasNextRecord(); i++){
//                Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
//                id = (String)aobj[40];
//
//                /*for(int z = 0; z < aobj.length; z++)
//                    System.out.println(z+"\t"+aobj[z]+" ");
//                if(id != null)
//                    System.exit(0);*/
//                if(!EDBldngCount.containsKey(id)){
//                    bed = new BuildingAreaPerED();
//                    EDBldngCount.put(id, bed);
//                }
//                else{
//                    bed = EDBldngCount.get(id);
//                }
//                bed.sqFootage.add(((Long)aobj[25]).doubleValue());
//                bed.bldCount++;
//            }
//
//            FileOutputStream fout = new FileOutputStream("building_area_per_ED.txt");
//            for(Iterator <String>i = EDBldngCount.keySet().iterator(); i.hasNext();){
//                String t = i.next();
//                bed = EDBldngCount.get(t);
//
//                String nline = t+"\t"+bed.bldCount+"\t"+bed.getMeanSqFt()+"\t"+bed.getStdevSqFt()
//                        +"\n";
//                System.out.println(nline);
//                fout.write((nline).getBytes());
//            }
//
//        } catch (JDBFException e) {
//            e.printStackTrace();
//        } catch(IOException io){
//            io.printStackTrace();
//        }
    }

    public int bldCount = 0;
    public double totalSqFootage = 0;
    public ArrayList<Double> sqFootage = new ArrayList<Double>();
    private double mean = 0;
    private double stdev = 0;
    public double getMeanSqFt(){
        if(mean == 0){
            for(Iterator <Double>i = sqFootage.iterator(); i.hasNext();){
                mean += i.next().doubleValue();
            }
            mean /= sqFootage.size();
            stdev = 0;
            for(Iterator <Double>i = sqFootage.iterator(); i.hasNext();){
                stdev += (Math.pow(i.next().doubleValue() - mean, 2));
            }
            stdev =  Math.sqrt(stdev);
        }
        return mean;
    }
    public double getStdevSqFt(){
        if(mean == 0)
            this.getMeanSqFt();
        return stdev;
    }

}
