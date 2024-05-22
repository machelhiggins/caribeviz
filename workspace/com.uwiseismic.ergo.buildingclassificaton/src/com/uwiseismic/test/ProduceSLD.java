package com.uwiseismic.test;

import java.awt.Color;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerException;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.UserLayer;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;

public class ProduceSLD {

	public static void main(String[] args) {
		try {
//			Style style = PolygonStyleTwo.getStructureTypeStyle();
//			Style style = edStyle();
			Style style = pointsRange();
//			Style style = createPointStyle();
			
			StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
			
			StyledLayerDescriptor sld = styleFactory.createStyledLayerDescriptor();
			UserLayer layer = styleFactory.createUserLayer();
			layer.setLayerFeatureConstraints(new FeatureTypeConstraint[] {null});
			sld.addStyledLayer(layer);
			layer.addUserStyle(style);

			SLDTransformer styleTransform = new SLDTransformer();
			String xml = styleTransform.transform(style);
			
			System.out.println(xml);
			System.out.println();
			
			try {
				System.out.println(java.net.URLDecoder.decode(ed, "UTF-16"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
		}
//		catch (URISyntaxException e) {
//			e.printStackTrace();
//		} 
		catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Style edStyle(){
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK),
                filterFactory.literal(1),
                filterFactory.literal(0.5));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(Color.GRAY),
                filterFactory.literal(0.0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
	}

	public static Style pointsRange(){
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
		
        Graphic gr = styleFactory.createDefaultGraphic();

        final int breaks[] = {
        		0,
        		250,
        		500,
        		750,
        		1000,
        		1250,        		
        };
        final Color[] colors = {
                new Color(0xfff5eb),
                new Color(0xfdd1a5),
                new Color(0xfd9243),
                new Color(0xde4f05),
                new Color(0x7f2704)                
        };
        
        Rule[] rules = new Rule[breaks.length];
        int i = 0;
        for (; i < colors.length; i++) {

            Mark mark = styleFactory.getCircleMark();
            mark.setStroke(styleFactory.createStroke(
                    filterFactory.literal(colors[i]), filterFactory.literal(1)));

            mark.setFill(styleFactory.createFill(filterFactory.literal(colors[i])));
            gr.graphicalSymbols().clear();
            gr.graphicalSymbols().add(mark);
            gr.setSize(filterFactory.literal(4));


            PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

            // create a rule and set the condition (value range) for which features
            // it will apply to
            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(sym);
            if(i+1 > colors.length){
            	Filter filter = ff.greater(ff.property("over_cap"), ff.literal(breaks[i]));
	            System.out.println(breaks[i]);
	            rule.setFilter(filter);
	
            }
            else{
	            Filter filter = ff.and(
	            		ff.greater(ff.property("over_cap"), ff.literal(breaks[i])),
	            		ff.lessOrEqual(ff.property("over_cap"), ff.literal(breaks[i+1])));
	            System.out.println(breaks[i]+"\t to "+breaks[i+1]);
//	            Filter filter = ff.lessOrEqual(ff.property("over_cap"), ff.literal(breaks[i+1]));
	            
	            rule.setFilter(filter);
	
            }
            rules[i] = rule;
        }
        
        Mark mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(colors[i-1]), filterFactory.literal(1)));

        mark.setFill(styleFactory.createFill(filterFactory.literal(colors[i-1])));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(4));


        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

        // create a rule and set the condition (value range) for which features
        // it will apply to
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);     
    	Filter filter = ff.greater(ff.property("over_cap"), ff.literal(breaks[i]));
        System.out.println(breaks[i]);
        rule.setFilter(filter);
        rules[i] = rule;
        
//        Rule rule1 =  styleBuilder.createRule(sym);
//        rule1.setName("rule1");
//        rule1.getDescription().setTitle("Shelter over capacity");
//	    rule1.getDescription().setAbstract("Rule for shelter over capacity");
//	    rule1.setFilter(ff.equals(ff.property("over_cap"),
//	    		ff.literal("C2")));
//	    
//	    mark = styleFactory.getCircleMark();
//        mark.setStroke(styleFactory.createStroke(
//                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
//        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
//        gr.graphicalSymbols().clear();
//        gr.graphicalSymbols().add(mark);
//        gr.setSize(filterFactory.literal(4));
//
//        Rule rule2 =  styleBuilder.createRule(sym);
//        rule2.setName("rule1");
//        rule2.getDescription().setTitle("Shelter over capacity");
//        rule2.getDescription().setAbstract("Rule for shelter over capacity");
//        rule2.setFilter(ff.equals(ff.property("over_cap"),
//	    		ff.literal("C2")));
//        
//	    mark = styleFactory.getCircleMark();
//        mark.setStroke(styleFactory.createStroke(
//                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
//        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
//        gr.graphicalSymbols().clear();
//        gr.graphicalSymbols().add(mark);
//        gr.setSize(filterFactory.literal(4));
//
//        Rule rule3 =  styleBuilder.createRule(sym);
//        rule3.setName("rule1");
//        rule3.getDescription().setTitle("Shelter over capacity");
//        rule3.getDescription().setAbstract("Rule for shelter over capacity");
//        rule3.setFilter(ff.equals(ff.property("over_cap"),
//	    		ff.literal("C2")));
//        
//	    mark = styleFactory.getCircleMark();
//        mark.setStroke(styleFactory.createStroke(
//                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
//        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
//        gr.graphicalSymbols().clear();
//        gr.graphicalSymbols().add(mark);
//        gr.setSize(filterFactory.literal(4));
//
//        Rule rule4 =  styleBuilder.createRule(sym);
//        rule4.setName("rule1");
//        rule4.getDescription().setTitle("Shelter over capacity");
//        rule4.getDescription().setAbstract("Rule for shelter over capacity");
//        rule4.setFilter(ff.equals(ff.property("over_cap"),
//	    		ff.literal("C2")));
//        
//	    mark = styleFactory.getCircleMark();
//        mark.setStroke(styleFactory.createStroke(
//                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
//        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
//        gr.graphicalSymbols().clear();
//        gr.graphicalSymbols().add(mark);
//        gr.setSize(filterFactory.literal(4));
//
//        Rule rule5 =  styleBuilder.createRule(sym);
//        rule5.setName("rule1");
//        rule5.getDescription().setTitle("Shelter over capacity");
//        rule5.getDescription().setAbstract("Rule for shelter over capacity");
//        rule5.setFilter(ff.equals(ff.property("over_cap"),
//	    		ff.literal("C2")));        
        
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rules);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
		
	}

    public static Style createPointStyle() {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
        Graphic gr = styleFactory.createDefaultGraphic();

        Mark mark = styleFactory.getCircleMark();

        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));

        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));

        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(5));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
    
    
    static String ed = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;sld:UserStyle xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"&gt; &lt;sld:Name&gt;Default Styler&lt;/sld:Name&gt; &lt;sld:FeatureTypeStyle&gt; &lt;sld:Name&gt;name&lt;/sld:Name&gt; &lt;sld:SemanticTypeIdentifier&gt;ergo-type:simple&lt;/sld:SemanticTypeIdentifier&gt; &lt;sld:Rule&gt; &lt;sld:PolygonSymbolizer&gt; &lt;sld:Fill&gt; &lt;sld:CssParameter name=\"fill-opacity\"&gt;0.0&lt;/sld:CssParameter&gt; &lt;/sld:Fill&gt; &lt;sld:Stroke/&gt; &lt;/sld:PolygonSymbolizer&gt; &lt;sld:TextSymbolizer&gt; &lt;sld:Font&gt; &lt;sld:CssParameter name=\"font-family\"&gt;Lucida Sans&lt;/sld:CssParameter&gt; &lt;sld:CssParameter name=\"font-size\"&gt;10.0&lt;/sld:CssParameter&gt; &lt;sld:CssParameter name=\"font-style\"&gt;normal&lt;/sld:CssParameter&gt; &lt;sld:CssParameter name=\"font-weight\"&gt;normal&lt;/sld:CssParameter&gt; &lt;/sld:Font&gt; &lt;sld:LabelPlacement&gt; &lt;sld:PointPlacement&gt; &lt;sld:AnchorPoint&gt; &lt;sld:AnchorPointX&gt;0.0&lt;/sld:AnchorPointX&gt; &lt;sld:AnchorPointY&gt;0.5&lt;/sld:AnchorPointY&gt; &lt;/sld:AnchorPoint&gt; &lt;/sld:PointPlacement&gt; &lt;/sld:LabelPlacement&gt; &lt;sld:Fill&gt; &lt;sld:CssParameter name=\"fill\"&gt;#000000&lt;/sld:CssParameter&gt; &lt;/sld:Fill&gt; &lt;/sld:TextSymbolizer&gt; &lt;/sld:Rule&gt; &lt;/sld:FeatureTypeStyle&gt; &lt;/sld:UserStyle&gt;";
	
}
