package com.uwiseismic.modifiedmappane.v4;

import java.awt.Color;
import java.net.URISyntaxException;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.opengis.filter.FilterFactory2;

/**
 * This class contains a style that is used by the footprints.
 */
public class PolygonStyleTwo {

	public static Style getStructureTypeStyle() throws URISyntaxException{
		
	    //
	    // We are using the GeoTools StyleBuilder that is helpful for quickly making things
	    	   

	    StyleBuilder styleBuilder = new StyleBuilder();
	    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
	    
	    PolygonSymbolizer polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.BLUE);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    // will create a default feature type style and rule etc...
	    //Style style = styleBuilder.createStyle(polygonSymbolizer);
	    
	    Rule rule1 = styleBuilder.createRule(polygonSymbolizer);
	    rule1.setName("rule1");
	    rule1.getDescription().setTitle("RM1");
	    rule1.getDescription().setAbstract("Rule for drawing cities");
	    rule1.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("RM1")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.RED);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule2 = styleBuilder.createRule(polygonSymbolizer);
	    rule2.setName("rule2");
	    rule2.getDescription().setTitle("RM2");
	    rule2.getDescription().setAbstract("Rule for drawing cities");
	    rule2.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("RM2")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.YELLOW);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule3 = styleBuilder.createRule(polygonSymbolizer);
	    rule3.setName("rule3");
	    rule3.getDescription().setTitle("C1");
	    rule3.getDescription().setAbstract("Rule for drawing cities");
	    rule3.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("C1")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.CYAN);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule4 = styleBuilder.createRule(polygonSymbolizer);
	    rule4.setName("rule4");
	    rule4.getDescription().setTitle("C2");
	    rule4.getDescription().setAbstract("Rule for drawing cities");
	    rule4.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("C2")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.WHITE);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule5 = styleBuilder.createRule(polygonSymbolizer);
	    rule5.setName("rule5");
	    rule5.getDescription().setTitle("PC1");
	    rule5.getDescription().setAbstract("Rule for drawing cities");
	    rule5.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("PC1")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.PINK);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule6 = styleBuilder.createRule(polygonSymbolizer);
	    rule6.setName("rule6");
	    rule6.getDescription().setTitle("W1");
	    rule6.getDescription().setAbstract("Rule for drawing cities");
	    rule6.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("W1")));
	    
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.BLACK);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule rule7 = styleBuilder.createRule(polygonSymbolizer);
	    rule7.setName("rule6");
	    rule7.getDescription().setTitle("S1");
	    rule7.getDescription().setAbstract("Rule for drawing cities");
	    rule7.setFilter(ff.equals(ff.property("struct_typ"),
	    		ff.literal("S1")));
	    
	    //** default
	    polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.GRAY);
	    polygonSymbolizer.getFill().setOpacity(ff.literal(0.5)); // 50% blue
	    polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLACK, 2.0));
	    
	    Rule ruleDefault = styleBuilder.createRule(polygonSymbolizer);
	    ruleDefault.setName("rule6");
	    ruleDefault.getDescription().setTitle("Unknown");
	    ruleDefault.getDescription().setAbstract("Rule for drawing cities");
	    ruleDefault.setIsElseFilter(true);
	    
	    //
	    // RULE 2 Default
//	    Graphic dotGraphic = builder.createGraphic(null, builder.createMark(StyleBuilder.MARK_CIRCLE),
//	            null);
//	    PointSymbolizer dotSymbolize = builder.createPointSymbolizer(dotGraphic);
//	    Rule rule2 = builder.createRule(dotSymbolize);
//	    rule2.setIsElseFilter(true);
	    
	    //
	    // define feature type styles used to actually define how features are rendered
	    Rule rules[] = new Rule[] { rule1, rule2, rule3, rule4, rule5, rule6, rule7, ruleDefault };
	    FeatureTypeStyle featureTypeStyle = styleBuilder.createFeatureTypeStyle("Feature", rules);
	    
	    //
	    // create a "user defined" style
	    Style style = styleBuilder.createStyle();
	    style.setName("style");
	    style.getDescription().setTitle("User Style");
	    style.getDescription().setAbstract("Definition of Style");
	    style.featureTypeStyles().add(featureTypeStyle);
	   
	    
	    return style;
	}
	
}
