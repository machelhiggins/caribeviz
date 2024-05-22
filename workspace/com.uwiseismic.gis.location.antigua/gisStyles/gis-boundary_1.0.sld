<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
   <sld:Name>Default Styler</sld:Name>
   <sld:Title>Default Styler</sld:Title>
   <sld:Abstract></sld:Abstract>
   <sld:FeatureTypeStyle>
      <sld:Name>name</sld:Name>
      <sld:Title>title</sld:Title>
      <sld:Abstract>abstract</sld:Abstract>
      <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
      <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
      <sld:SemanticTypeIdentifier>maeviz-type:simple</sld:SemanticTypeIdentifier>
      <sld:Rule>
         <sld:Name>name</sld:Name>
         <sld:Title>title</sld:Title>
         <sld:Abstract>Abstract</sld:Abstract>
         <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
         <sld:PolygonSymbolizer>
            <sld:Fill>
               <sld:CssParameter name="fill">
                  <ogc:Literal>#C0C0C0</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="fill-opacity">
                  <ogc:Literal>0.15</ogc:Literal>
               </sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
               <sld:CssParameter name="stroke">
                  <ogc:Literal>#808080</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="stroke-linecap">
                  <ogc:Literal>butt</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="stroke-linejoin">
                  <ogc:Literal>miter</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="stroke-opacity">
                  <ogc:Literal>1.0</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="stroke-width">
                  <ogc:Literal>2</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="stroke-dashoffset">
                  <ogc:Literal>0</ogc:Literal>
               </sld:CssParameter>
            </sld:Stroke>
         </sld:PolygonSymbolizer>
         <sld:TextSymbolizer>
            <sld:Font>
               <sld:CssParameter name="font-family">
                  <ogc:Literal>Lucida Sans</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="font-size">
                  <ogc:Literal>10.0</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="font-style">
                  <ogc:Literal>normal</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="font-weight">
                  <ogc:Literal>normal</ogc:Literal>
               </sld:CssParameter>
            </sld:Font>
            <sld:LabelPlacement>
               <sld:PointPlacement>
                  <sld:AnchorPoint>
                     <sld:AnchorPointX>
                        <ogc:Literal>0.0</ogc:Literal>
                     </sld:AnchorPointX>
                     <sld:AnchorPointY>
                        <ogc:Literal>0.5</ogc:Literal>
                     </sld:AnchorPointY>
                  </sld:AnchorPoint>
                  <sld:Displacement>
                     <sld:DisplacementX>
                        <ogc:Literal>0</ogc:Literal>
                     </sld:DisplacementX>
                     <sld:DisplacementY>
                        <ogc:Literal>0</ogc:Literal>
                     </sld:DisplacementY>
                  </sld:Displacement>
                  <sld:Rotation>
                     <ogc:Literal>0</ogc:Literal>
                  </sld:Rotation>
               </sld:PointPlacement>
            </sld:LabelPlacement>
            <sld:Fill>
               <sld:CssParameter name="fill">
                  <ogc:Literal>#000000</ogc:Literal>
               </sld:CssParameter>
               <sld:CssParameter name="fill-opacity">
                  <ogc:Literal>1.0</ogc:Literal>
               </sld:CssParameter>
            </sld:Fill>
         </sld:TextSymbolizer>
      </sld:Rule>
   </sld:FeatureTypeStyle>
</sld:UserStyle>
