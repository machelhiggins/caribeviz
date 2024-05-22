<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld"
	xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
	xmlns:gml="http://www.opengis.net/gml">
	<sld:Name>Default Styler</sld:Name>
	<sld:Title>Default Styler</sld:Title>
	<sld:FeatureTypeStyle>
		<sld:Name>name</sld:Name>
		<sld:Title>title</sld:Title>
		<sld:Abstract>abstract</sld:Abstract>
		<sld:FeatureTypeName>Feature</sld:FeatureTypeName>
		<sld:SemanticTypeIdentifier>generic:geometry
		</sld:SemanticTypeIdentifier>
		<sld:SemanticTypeIdentifier>colorbrewer:RdBu
		</sld:SemanticTypeIdentifier>
		<sld:SemanticTypeIdentifier>ergo-type:ranged
		</sld:SemanticTypeIdentifier>
		<sld:Rule>
			<sld:Name>rule01</sld:Name>
			<sld:Title>0.07 to 0.23</sld:Title>
			<sld:Abstract>Abstract</sld:Abstract>
			<ogc:Filter>
				<ogc:And>
					<ogc:PropertyIsGreaterThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>0</ogc:Literal>
					</ogc:PropertyIsGreaterThan>
					<ogc:PropertyIsLessThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>250</ogc:Literal>
					</ogc:PropertyIsLessThan>
				</ogc:And>
			</ogc:Filter>
			<sld:MaxScaleDenominator>1.7976931348623157E308
			</sld:MaxScaleDenominator>
			<sld:PointSymbolizer>
				<sld:Graphic>
					<sld:Mark>
						<sld:Fill>
							<sld:CssParameter name="fill">#0571B0
							</sld:CssParameter>
							<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke />
					</sld:Mark>
					<sld:Size>8</sld:Size>
				</sld:Graphic>
			</sld:PointSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule02</sld:Name>
			<sld:Title>0.23 to 0.39</sld:Title>
			<sld:Abstract>Abstract</sld:Abstract>
			<ogc:Filter>
				<ogc:And>
					<ogc:PropertyIsGreaterThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>250</ogc:Literal>
					</ogc:PropertyIsGreaterThan>
					<ogc:PropertyIsLessThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>500</ogc:Literal>
					</ogc:PropertyIsLessThan>
				</ogc:And>
			</ogc:Filter>
			<sld:MaxScaleDenominator>1.7976931348623157E308
			</sld:MaxScaleDenominator>
			<sld:PointSymbolizer>
				<sld:Graphic>
					<sld:Mark>
						<sld:Fill>
							<sld:CssParameter name="fill">#92C5DE
							</sld:CssParameter>
							<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke />
					</sld:Mark>
					<sld:Size>8</sld:Size>
				</sld:Graphic>
			</sld:PointSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule03</sld:Name>
			<sld:Title>0.39 to 0.55</sld:Title>
			<sld:Abstract>Abstract</sld:Abstract>
			<ogc:Filter>
				<ogc:And>
					<ogc:PropertyIsGreaterThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>500</ogc:Literal>
					</ogc:PropertyIsGreaterThan>
					<ogc:PropertyIsLessThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>750</ogc:Literal>
					</ogc:PropertyIsLessThan>
				</ogc:And>
			</ogc:Filter>
			<sld:MaxScaleDenominator>1.7976931348623157E308
			</sld:MaxScaleDenominator>
			<sld:PointSymbolizer>
				<sld:Graphic>
					<sld:Mark>
						<sld:Fill>
							<sld:CssParameter name="fill">#F7F7F7
							</sld:CssParameter>
							<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke />
					</sld:Mark>
					<sld:Size>8</sld:Size>
				</sld:Graphic>
			</sld:PointSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule04</sld:Name>
			<sld:Title>0.55 to 0.71</sld:Title>
			<sld:Abstract>Abstract</sld:Abstract>
			<ogc:Filter>
				<ogc:And>
					<ogc:PropertyIsGreaterThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>750</ogc:Literal>
					</ogc:PropertyIsGreaterThan>
					<ogc:PropertyIsLessThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>1000</ogc:Literal>
					</ogc:PropertyIsLessThan>
				</ogc:And>
			</ogc:Filter>
			<sld:MaxScaleDenominator>1.7976931348623157E308
			</sld:MaxScaleDenominator>
			<sld:PointSymbolizer>
				<sld:Graphic>
					<sld:Mark>
						<sld:Fill>
							<sld:CssParameter name="fill">#F4A582
							</sld:CssParameter>
							<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke />
					</sld:Mark>
					<sld:Size>8</sld:Size>
				</sld:Graphic>
			</sld:PointSymbolizer>
		</sld:Rule>
		<sld:Rule>
			<sld:Name>rule05</sld:Name>
			<sld:Title>0.71 to 0.87</sld:Title>
			<sld:Abstract>Abstract</sld:Abstract>
			<ogc:Filter>
				<ogc:And>
					<ogc:PropertyIsGreaterThan>
						<ogc:PropertyName>popdisloc</ogc:PropertyName>
						<ogc:Literal>1000</ogc:Literal>
					</ogc:PropertyIsGreaterThan>
				</ogc:And>
			</ogc:Filter>
			<sld:MaxScaleDenominator>1.7976931348623157E308
			</sld:MaxScaleDenominator>
			<sld:PointSymbolizer>
				<sld:Graphic>
					<sld:Mark>
						<sld:Fill>
							<sld:CssParameter name="fill">#D6604D
							</sld:CssParameter>
							<sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
						</sld:Fill>
						<sld:Stroke />
					</sld:Mark>
					<sld:Size>8</sld:Size>
				</sld:Graphic>
			</sld:PointSymbolizer>
		</sld:Rule>
	</sld:FeatureTypeStyle>
</sld:UserStyle>