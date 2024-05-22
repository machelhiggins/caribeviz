package com.uwiseismic.ergo.gis;

import java.awt.Color;
import java.util.Formatter;
import java.util.Locale;


import org.geotools.filter.FilterFactoryImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.Symbolizer;

public class PolygonStyle {
	
	private static FilterFactoryImpl filterFactory = new FilterFactoryImpl();
	private static StyleFactoryImpl styleFactory = new StyleFactoryImpl();
    private static StyleBuilder styleBuilder = new StyleBuilder( styleFactory, filterFactory );
    private static Formatter formatter = new Formatter(new StringBuilder(), Locale.US);
	
	public static Style getStyle(Color edgeColour, Color fillColour, double strokeWidth, double  opacity){
		return getStyle(formatter.format("0x%x%x%x", edgeColour.getRed(),edgeColour.getGreen(), edgeColour.getBlue())
				.toString(),
				formatter.format("0x%x%x%x", fillColour.getRed(),fillColour.getGreen(), fillColour.getBlue())
				.toString(),
				strokeWidth, opacity);
	}
	
	/**
	 * 
	 * For some reason, exceptions are thrown when setting Expressions with Color objects. Using the string rgb representation
	 * accpeted by java.awt.Colow.decode(...) works and I dont have time to figure out why.
	 * 
	 * @param edgeColour
	 * @param fillColour
	 * @param strokeWidth
	 * @param opacity
	 * @return
	 */
	protected static Style getStyle(String edgeColour, String fillColour, double strokeWidth, double  opacity){
		 Stroke stroke = styleFactory.createStroke(
				 styleBuilder.literalExpression(edgeColour),
				 styleBuilder.literalExpression(edgeColour),
				 styleBuilder.literalExpression(0.9));	
		
		 Fill fill = styleFactory.createFill(
				 styleBuilder.literalExpression(edgeColour),
				 styleBuilder.literalExpression(opacity));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		PolygonSymbolizer sym = styleBuilder.createPolygonSymbolizer(stroke, fill);
		//PolygonSymbolizer sym2 = styleBuilder.crea
	
		//FeatureTypeStyle fts = styleBuilder.createFeatureTypeStyle(sym);
		Style style = styleBuilder.createStyle(sym);
		//style.setFeatureTypeStyles(new FeatureTypeStyle[] {fts}); 	
		return style;
	}
	
}
