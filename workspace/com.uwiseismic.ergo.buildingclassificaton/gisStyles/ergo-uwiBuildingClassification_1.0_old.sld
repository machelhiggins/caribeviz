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
      <sld:SemanticTypeIdentifier>colorbrewer:RdBu</sld:SemanticTypeIdentifier>
      <sld:SemanticTypeIdentifier>ergo-type:ranged</sld:SemanticTypeIdentifier>
      <sld:FeatureTypeStyle>
        <sld:Rule>
          <sld:Name>C1</sld:Name>
          <sld:Description>
            <sld:Title>C1</sld:Title>
          </sld:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>struct_typ</ogc:PropertyName>
              <ogc:Literal>C1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Fill>
                  <sld:SvgParameter name="fill">#b140d6</sld:SvgParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:SvgParameter name="stroke">#000000</sld:SvgParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>1</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <sld:Name>PC1</sld:Name>
          <sld:Description>
            <sld:Title>PC1</sld:Title>
          </sld:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>struct_typ</ogc:PropertyName>
              <ogc:Literal>PC1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Fill>
                  <sld:SvgParameter name="fill">#bbd428</sld:SvgParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:SvgParameter name="stroke">#000000</sld:SvgParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>2</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <sld:Name>RM</sld:Name>
          <sld:Description>
            <sld:Title>RM</sld:Title>
          </sld:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>struct_typ</ogc:PropertyName>
              <ogc:Literal>RM</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Fill>
                  <sld:SvgParameter name="fill">#cf6268</sld:SvgParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:SvgParameter name="stroke">#000000</sld:SvgParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>2</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <sld:Name>S</sld:Name>
          <sld:Description>
            <sld:Title>S</sld:Title>
          </sld:Description>
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>struct_typ</ogc:PropertyName>
              <ogc:Literal>S</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Fill>
                  <sld:SvgParameter name="fill">#488ce4</sld:SvgParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:SvgParameter name="stroke">#000000</sld:SvgParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>2</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
   </sld:FeatureTypeStyle>
</sld:UserStyle>
