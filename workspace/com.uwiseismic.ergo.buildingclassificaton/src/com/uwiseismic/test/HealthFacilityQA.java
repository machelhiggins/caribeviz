/*
 * Created on Jul 8, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

//import com.hexiong.jdbf.DBFReader;

public class HealthFacilityQA {

        public static void main(String args[]){
            try{

//                File dir = new File("C:\\projects\\MaevizRoadNetworkScratch\\casualties_facilities");
//                File listing[] = dir.listFiles();
//
//                HashMap <String, ArrayList> idOcc = new HashMap <String, ArrayList>();
//                HashMap <String, ArrayList> idOver = new HashMap <String, ArrayList>();
//                ArrayList <Double>occ;
//                ArrayList <Double>overCap;
//                String id;
//                for(int z = 0; z < listing.length; z++){
//                    if(listing[z].getName().matches(".*dbf$")){
//                        String sampleFile = listing[z].getAbsolutePath();
//                        System.err.println(sampleFile);
//                        DBFReader dbfreader = new DBFReader(sampleFile);
//
//                        for(int i = 0; dbfreader.hasNextRecord(); i++){
//                            Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
//                            id = (String)aobj[2];
//                            if(!idOcc.containsKey(id)){
//                                idOcc.put(id, new ArrayList<Double>());
//                                idOver.put(id, new ArrayList<Double>());
//                            }
//                            occ = idOcc.get(id);
//                            overCap = idOver.get(id);
//
//                            occ.add(new Double((Double)aobj[4]));
//                            overCap.add(new Double((Double)aobj[5]));
//                        }
//                    }
//                }
//
//                for(Iterator <String>i = idOcc.keySet().iterator(); i.hasNext();){
//                    id = i.next();
//                    occ = idOcc.get(id);
//                    overCap = idOver.get(id);
//                    double occd[] = new double[occ.size()];
//                    double over[] = new double[occ.size()];
//                    double meanOcc = 0;
//                    double stdOcc = 0;
//                    int hm = 0;
//                    for(Iterator <Double>n = occ.iterator();n.hasNext();){
//                        occd[hm] = n.next().doubleValue();
//                        meanOcc+= occd[hm];
//                        hm++;
//                    }
//                    meanOcc /= occd.length;
//                    for(hm = 0; hm < occd.length;hm++){
//                        stdOcc += Math.pow(occd[hm] - meanOcc, 2);
//                    }
//                    stdOcc = Math.sqrt(stdOcc);
//
//                    double meanOver = 0;
//                    double stdOver = 0;
//                    hm = 0;
//                    for(Iterator <Double>n = overCap.iterator();n.hasNext();){
//                        over[hm] = n.next().doubleValue();
//
//                        meanOver+= over[hm];
//                        hm++;
//                    }
//                    meanOver /= over.length;
//                    for(hm = 0; hm < over.length;hm++){
//                        stdOver += Math.pow(over[hm] - meanOver, 2);
//                    }
//                    stdOver = Math.sqrt(stdOver);
//                    System.out.println(id+"\t"+meanOcc+"\t"+stdOcc+"\tover "+meanOver+"\t"+stdOver);
//
//
//                }


            }catch(Exception ex){
                ex.printStackTrace();

            }
        }
}
