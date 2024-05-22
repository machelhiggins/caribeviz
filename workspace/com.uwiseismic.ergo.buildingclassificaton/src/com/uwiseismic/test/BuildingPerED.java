/*
 * Created on Aug 6, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import com.hexiong.jdbf.DBFReader;

public class BuildingPerED {
    public static void main(String args[]){
//        try{
//
//
//            HashMap <String, BuildingPerED> EDBldngCount = new HashMap <String, BuildingPerED>();
//            String EDCode = "EDCode";
//            String pop = "TOTPOP";
//
//            String id;
//
//            String sampleFile = "J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\Caribeviz Building Inventory\\Kingston_StAnd_test_guess_EDPOP.dbf";
//            DBFReader dbfreader = new DBFReader(sampleFile);
//            BuildingPerED bed;
//
//            for(int i = 0; dbfreader.hasNextRecord(); i++){
//                Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
//                id = (String)aobj[49];
//
//               /* for(int z = 0; z < aobj.length; z++)
//                    System.out.println(z+"\t"+aobj[z]+" ");
//                if(id != null)
//                    System.exit(0);*/
//                if(!EDBldngCount.containsKey(id)){
//                    bed = new BuildingPerED();
//                    bed.bldCount++;
//                    bed.pop = ((Long)aobj[48]).longValue();
//                    EDBldngCount.put(id, bed);
//                }
//                else{
//                    bed = EDBldngCount.get(id);
//                    bed.pop = ((Long)aobj[48]).longValue();
//                    bed.bldCount++;
//                }
//            }
//
//            String ofile= "J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\StatinData_convert-wgs84\\stAndrew\\StAndrew_EDs_WGS_Project_EDPOP.dbf";
//            dbfreader = new DBFReader(ofile);
//
//            for(int i = 0; dbfreader.hasNextRecord(); i++){
//                Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
//                id = (String)aobj[1];
//                //System.out.println(id);
//               /* for(int z = 0; z < aobj.length; z++)
//                    System.out.println(z+"\t"+aobj[z]+" ");
//                if(id != null)
//                    System.exit(0);*/
//                if(!EDBldngCount.containsKey(id)){
//                    bed = new BuildingPerED();
//                    bed.area = ((Double)aobj[2]).doubleValue();
//                    EDBldngCount.put(id, bed);
//                }
//                else{
//                    bed = EDBldngCount.get(id);
//                    bed.area = ((Double)aobj[2]).doubleValue();
//                }
//            }
//
//
//            ofile= "J:\\HAZARD_PROJECT\\DRRC_RISK_ATLAS\\data\\Jamaica\\StatinData_convert-wgs84\\Kgn\\Kingston_EDs_WGS_Project_EDPOP.dbf";
//            dbfreader = new DBFReader(ofile);
//
//            for(int i = 0; dbfreader.hasNextRecord(); i++){
//                Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
//                id = (String)aobj[1];
//                //System.out.println(id);
//               /* for(int z = 0; z < aobj.length; z++)
//                    System.out.println(z+"\t"+aobj[z]+" ");
//                if(id != null)
//                    System.exit(0);*/
//                if(!EDBldngCount.containsKey(id)){
//                    bed = new BuildingPerED();
//                    bed.area = ((Double)aobj[2]).doubleValue();
//                    EDBldngCount.put(id, bed);
//                }
//                else{
//                    bed = EDBldngCount.get(id);
//                    bed.area = ((Double)aobj[2]).doubleValue();
//                }
//            }
//
//            FileOutputStream fout = new FileOutputStream("DELETEME.txt");
//            FileOutputStream fout2 = new FileOutputStream("DELETEME_TOO.txt");
//            for(Iterator <String>i = EDBldngCount.keySet().iterator(); i.hasNext();){
//                String t = i.next();
//                bed = EDBldngCount.get(t);
//                if(bed.area == 0)
//                    continue;
//                String nline = t+"\t"+bed.bldCount+"\t"+bed.pop+"\t"+bed.area+"\t"+(bed.bldCount/bed.area)+"\t"+(bed.pop/bed.area)+"\t"+(bed.bldCount >0 ? bed.pop/bed.bldCount : 0)
//                        +"\n";
//                System.out.println(nline);
//                fout.write((nline).getBytes());
//                String r = "residential";
//                String c = "commercial";
//                if(true
//                        && (bed.pop/bed.area) <= 8
//                        && (bed.bldCount >0 ? bed.pop/bed.bldCount : 0) <= 0.0077
//                        ){
//                    nline = t+"\tcommercial\n";
//                }else
//                    nline = t+"\tresidential\n";
//                fout2.write(nline.getBytes());
//            }
//
//        }catch(Exception ex){
//            ex.printStackTrace();
//
//        }
    }


    public long pop = 0;
    public int bldCount = 0;
    public double area = 0;

}
