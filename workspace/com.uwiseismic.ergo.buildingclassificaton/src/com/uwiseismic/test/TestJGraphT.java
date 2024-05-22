package com.uwiseismic.test;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;
public class TestJGraphT  extends DefaultWeightedEdge implements Comparable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 787690918185574492L;
	private String id;
	
	public TestJGraphT(String id){
		this.id = id;
	}
	
	public String getID(){
		return id;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
        WeightedPseudograph <String, DefaultWeightedEdge>graph = 
        		new <String, DefaultWeightedEdge>WeightedPseudograph(DefaultWeightedEdge.class);
        String a = "1";
        graph.addVertex(a);
        String b = "2";
        graph.addVertex(b);
        String c = "3";
        graph.addVertex(c);
        String d = "4";
        graph.addVertex(d);
        String e = "5";
        graph.addVertex(e);

        TestJGraphT edge1 = new TestJGraphT("edge 1");
        graph.addEdge(a, b, edge1);
        graph.setEdgeWeight(edge1, 2);
        
        TestJGraphT edge2 = new TestJGraphT("edge 2");
        graph.addEdge(b, e, edge2);
        graph.setEdgeWeight(edge2, 7);
        
        TestJGraphT edge3 = new TestJGraphT("edge 3");
        graph.addEdge(a, c, edge3);
        graph.setEdgeWeight(edge3, 3);
        
        TestJGraphT edge4 = new TestJGraphT("edge 4");
        graph.addEdge(b, c, edge4);
        graph.setEdgeWeight(edge4, 2);
        
        TestJGraphT edge5 = new TestJGraphT("edge 5");
        graph.addEdge(b, d, edge5);
        graph.setEdgeWeight(edge5, 1);
        
        TestJGraphT edge6 = new TestJGraphT("edge 6");
        graph.addEdge(d, e, edge6);
        graph.setEdgeWeight(edge6, 1);
        
        TestJGraphT edge7 = new TestJGraphT("edge 7");
        graph.addEdge(c, d, edge7);
        graph.setEdgeWeight(edge7, 2);
        
        
        GraphPath<String, DefaultWeightedEdge> pathway = DijkstraShortestPath.findPathBetween(graph, a, e);

        System.out.println(pathway.getLength());
        System.out.println(pathway.getWeight());
        for(String vertex: pathway.getVertexList()){
        	System.out.println("Node "+vertex);
        }
	}

	@Override
	public int compareTo(Object obj) {
		return this.id.compareTo(((TestJGraphT)obj).getID());
	}


}