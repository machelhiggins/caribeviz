<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld"
	xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
	xmlns:gml="http://www.opengis.net/gml">
	<sld:Name>Default Styler</sld:Name>
	<sld:FeatureTypeStyle>
		<sld:Name>name</sld:Name>
		<sld:SemanticTypeIdentifier>ergo-type:simple
		</sld:SemanticTypeIdentifier>
		<sld:Rule>
			<sld:PolygonSymbolizer>
				<sld:Fill>
					<sld:CssParameter name="fill-opacity">0.0</sld:CssParameter>
				</sld:Fill>
				<sld:Stroke />
			</sld:PolygonSymbolizer>
			<sld:TextSymbolizer>
				<sld:Font>
					<sld:CssParameter name="font-family">Lucida Sans
					</sld:CssParameter>
					<sld:CssParameter name="font-size">10.0</sld:CssParameter>
					<sld:CssParameter name="font-style">normal</sld:CssParameter>
					<sld:CssParameter name="font-weight">normal</sld:CssParameter>
				</sld:Font>
				<sld:LabelPlacement>
					<sld:PointPlacement>
						<sld:AnchorPoint>
							<sld:AnchorPointX>0.0</sld:AnchorPointX>
							<sld:AnchorPointY>0.5</sld:AnchorPointY>
						</sld:AnchorPoint>
					</sld:PointPlacement>
				</sld:LabelPlacement>
				<sld:Fill>
					<sld:CssParameter name="fill">#000000</sld:CssParameter>
				</sld:Fill>
			</sld:TextSymbolizer>
		</sld:Rule>
	</sld:FeatureTypeStyle>
</sld:UserStyle>