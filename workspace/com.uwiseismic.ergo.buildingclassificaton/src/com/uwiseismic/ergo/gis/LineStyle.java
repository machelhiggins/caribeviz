package com.uwiseismic.ergo.gis;

import java.awt.Color;
import java.util.Formatter;
import java.util.Locale;

import org.geotools.filter.FilterFactoryImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.Symbolizer;

public class LineStyle {
	
	private static FilterFactoryImpl filterFactory = new FilterFactoryImpl();
	private static StyleFactoryImpl styleFactory = new StyleFactoryImpl();
    private static StyleBuilder styleBuilder = new StyleBuilder( styleFactory, filterFactory );
    private static Formatter formatter = new Formatter(new StringBuilder(), Locale.US);
	

	public static Style getStyle(Color edgeColour, double strokeWidth, double  opacity){
		 Stroke stroke = styleFactory.createStroke(
				 styleBuilder.literalExpression(edgeColour),
				 styleBuilder.literalExpression(edgeColour),
				 styleBuilder.literalExpression(0.9));			

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		LineSymbolizer sym = styleBuilder.createLineSymbolizer(edgeColour, strokeWidth);//styleBuilder.createPolygonSymbolizer(stroke, fill);

		Style style = styleBuilder.createStyle(sym);
		return style;
	}
}
