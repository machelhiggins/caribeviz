package com.uwiseismic.ergo.gis;

import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;


import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.Envelope2D;

public class RasterCreator {

	public static GridCoverage2D createRaster(Envelope2D envelope, double rasterData[][], int cols, int rows, String rasterName) throws Exception{
		double bufferData[] = new double[cols*rows];
		
		for(int i=0; i < cols; i++)
			System.arraycopy(rasterData[i], 0, bufferData, i*cols, rows);		
		
		
		ColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false,
				ColorModel.TRANSLUCENT, DataBuffer.TYPE_DOUBLE);		
		DataBuffer buffer = new DataBufferDouble(bufferData, bufferData.length);		              
        SampleModel sample = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, cols, rows, 1);
        //WritableRaster raster = RasterFactory.createBandedRaster( DataBuffer.TYPE_DOUBLE, cols, rows, 1, null );
        WritableRaster raster = Raster.createWritableRaster(sample, buffer, null);
        BufferedImage img = new BufferedImage(colorModel, raster, false, null);
        GridCoverageFactory factory = new GridCoverageFactory();
        //GridCoverage2D cov2D = factory.create(rasterName, raster, envelope);  
        return factory.create(rasterName, img, envelope);
	}
	
	
}
