package com.uwiseismic.ergo.roadnetwork;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

import com.uwiseismic.ergo.graph.ErgoGraphEdge;
import com.uwiseismic.gis.util.SimplePoint;


public class RoadGraphEdge implements ErgoGraphEdge, SimpleFeature, Comparable<RoadGraphEdge>{
	
	private SimpleFeature edge;	
	private int startNodeID;
	private int endNodeID;
	private double wayDist = -1;
	private SimplePoint estimatedCenter;
	private int weight = 1;
	public static int MAX_WEIGHT = 3;
	
	public RoadGraphEdge(SimpleFeature edge, int startNodeID, int endNodeID){
		this.edge = edge;
		this.startNodeID = startNodeID;
		this.endNodeID = endNodeID;
		int roadType = RoadNetworkConstants.getRoadType(edge);
		if(roadType == RoadNetworkConstants.HIGHWAY)
			weight = MAX_WEIGHT;
		else if(roadType == RoadNetworkConstants.PRIMARY)
			weight = 2;
		else if(roadType == RoadNetworkConstants.SECONDARY)
			weight = 1;
		else if(roadType == RoadNetworkConstants.RESIDENTIAL)
			weight = 1;
	}
	
	public void setEstimatedCenter(SimplePoint estimatedCenter){
		this.estimatedCenter = estimatedCenter;
	}
	
	public SimplePoint getEstimatedCenter(){
		return estimatedCenter;
	}	
	
	public void setWayDistance(double wayDist){
		this.wayDist = wayDist;
	}
	
	public double getWayDistance(){
		return wayDist;
	}
	
	public int getStartNodeID() {
		return startNodeID;
	}

	public int getEndNodeID() {
		return endNodeID;
	}

	public long getOSMID(){
		return Long.parseLong(edge.getAttribute("osm_id").toString());
	}
	
	/**
	 * 
	 * Will return -1 if the nodeID doesn't match start or end node
	 * @param nodeID
	 * @return
	 */
	public int getOtherNode(int nodeID){
		if(nodeID == startNodeID)
			return endNodeID;
		else if(nodeID == endNodeID) 
			return startNodeID;
		else
			return -1;
	}
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	//** below are unnecessarily overriding methods BUT just in case we want to save a duplicate data set during analysis
	@Override
	public BoundingBox getBounds() {
		return edge.getBounds();
	}

	@Override
	public GeometryAttribute getDefaultGeometryProperty() {
		return edge.getDefaultGeometryProperty();
	}

	@Override
	public FeatureId getIdentifier() {
		return edge.getIdentifier();
	}

	@Override
	public void setDefaultGeometryProperty(GeometryAttribute arg0) {
		edge.setDefaultGeometryProperty(arg0);
	}

	@Override
	public Collection<Property> getProperties() {
		return edge.getProperties();
	}

	@Override
	public Collection<Property> getProperties(Name arg0) {
		return edge.getProperties(arg0);
	}

	@Override
	public Collection<Property> getProperties(String arg0) {
		return edge.getProperties(arg0);
	}

	@Override
	public Property getProperty(Name arg0) {
		return edge.getProperty(arg0);
	}

	@Override
	public Property getProperty(String arg0) {
		return edge.getProperty(arg0);
	}

	@Override
	public Collection<? extends Property> getValue() {
		return edge.getValue();
	}

	@Override
	public void setValue(Collection<Property> arg0) {
		edge.setValue(arg0);
	}

	@Override
	public void validate() throws IllegalAttributeException {
		edge.validate();
	}

	@Override
	public AttributeDescriptor getDescriptor() {
		return edge.getDescriptor();
	}

	@Override
	public Name getName() {
		return edge.getName();
	}

	@Override
	public Map<Object, Object> getUserData() {
		return edge.getUserData();
	}

	@Override
	public boolean isNillable() {
		return edge.isNillable();
	}

	@Override
	public void setValue(Object arg0) {
		edge.setValue(arg0);
	}

	@Override
	public Object getAttribute(String arg0) {
		return edge.getAttribute(arg0);
	}

	@Override
	public Object getAttribute(Name arg0) {
		return edge.getAttribute(arg0);
	}

	@Override
	public Object getAttribute(int arg0) throws IndexOutOfBoundsException {
		return edge.getAttribute(arg0);
	}

	@Override
	public int getAttributeCount() {
		return edge.getAttributeCount();
	}

	@Override
	public List<Object> getAttributes() {
		return edge.getAttributes();
	}

	@Override
	public Object getDefaultGeometry() {
		return edge.getDefaultGeometry();
	}

	@Override
	public SimpleFeatureType getFeatureType() {
		return edge.getFeatureType();
	}

	@Override
	public String getID() {
		return edge.getID();
	}

	@Override
	public SimpleFeatureType getType() {
		return edge.getType();
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		edge.setAttribute(arg0, arg1);
	}

	@Override
	public void setAttribute(Name arg0, Object arg1) {
		edge.setAttribute(arg0, arg1);
	}

	@Override
	public void setAttribute(int arg0, Object arg1) throws IndexOutOfBoundsException {
		edge.setAttribute(arg0, arg1);
	}

	@Override
	public void setAttributes(List<Object> arg0) {
		edge.setAttributes(arg0);
	}

	@Override
	public void setAttributes(Object[] arg0) {
		edge.setAttributes(arg0);
	}

	@Override
	public void setDefaultGeometry(Object arg0) {
		edge.setDefaultGeometry(arg0);
	}

	public boolean equals(Object o){
		RoadGraphEdge ob = (RoadGraphEdge)o;
		return this.estimatedCenter.equals(ob.getEstimatedCenter());
	}

	@Override
	public int compareTo(RoadGraphEdge o) {
		return this.estimatedCenter.compareTo(o.getEstimatedCenter());
	}
}
