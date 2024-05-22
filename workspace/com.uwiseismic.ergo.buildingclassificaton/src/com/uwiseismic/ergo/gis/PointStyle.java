package com.uwiseismic.ergo.gis;

import java.awt.Color;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.Symbolizer;

public class PointStyle {
	
	private static FilterFactoryImpl filterFactory = new FilterFactoryImpl();
	private static StyleFactoryImpl styleFactory = new StyleFactoryImpl();
    private static StyleBuilder styleBuilder = new StyleBuilder( styleFactory, filterFactory );
	
	public static Style getStyle(Color edgeColour, Color fillColour, int size, double strokeWidth){
		//FilterFactoryImpl filterFactory = CommonFactoryFinder.getFilterFactory(null);

		Graphic gr = styleFactory.createDefaultGraphic();

		Mark mark = styleFactory.getCircleMark();

		mark.setStroke(styleFactory.createStroke(
				filterFactory.literal(edgeColour), filterFactory.literal(1)));

		mark.setFill(styleFactory.createFill(filterFactory.literal(fillColour)));

		gr.addMark(mark);
		gr.setSize(filterFactory.literal(5));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

		Rule rule = styleBuilder.createRule(sym);
		
		rule.setSymbolizers(new Symbolizer[]{sym});
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
		Style style = styleFactory.createStyle();
		style.setFeatureTypeStyles(new FeatureTypeStyle[] {fts}); 
		return style;
	}
}
