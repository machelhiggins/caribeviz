<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld"
	xmlns:sld="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml"
	xmlns:ogc="http://www.opengis.net/ogc">
	<sld:Name>style</sld:Name>
	<sld:Title>User Style</sld:Title>
	<sld:Abstract>Definition of Style</sld:Abstract>
	<sld:FeatureTypeStyle>
		<sld:Name>buildingClassification</sld:Name>
		<sld:FeatureTypeName>Feature</sld:FeatureTypeName>
		<sld:Rule>
			<sld:Name>rule1</sld:Name>
			<sld:Title>RM</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>RM</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#E29965</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule3</sld:Name>
			<sld:Title>C1</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>C1</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#13CBCB</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule4</sld:Name>
			<sld:Title>C2</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>C2</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#DD3F84</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule5</sld:Name>
			<sld:Title>PC1</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>PC1</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#B9EE27</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule6</sld:Name>
			<sld:Title>W1</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>W1</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#05DF7D</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule6</sld:Name>
			<sld:Title>S1</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<ogc:Filter>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>struct_typ</ogc:PropertyName>
					<ogc:Literal>S1</ogc:Literal>
				</ogc:PropertyIsEqualTo>
			</ogc:Filter>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#7293D2</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule6</sld:Name>
			<sld:Title>Unknown</sld:Title>
			<sld:Abstract>Rule for drawing cities</sld:Abstract>
			<sld:ElseFilter />
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill">#C8D1C8</sld:CssParameter>
					<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
		</sld:Rule>
	</sld:FeatureTypeStyle>
</sld:UserStyle>
