package rCSP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

public class PulseGraph  implements Graph<VertexPulse, EdgePulse> {

	private static  int[] C;

	static VertexPulse[] vertexes;
	
	/*static int estMT=0;
	static int estMD=0;
	static int estBound=0;
	static int estLABELS=0;
	static String megaString;
	*/
	
	private int numNodes;
	private int Cd;
	private int Ct;
	
	/**
	 * Pulse Stuff
	 */
	public static int[][] nadir;
//	static int MaxTime;
//	static int MaxDist;
//	static int MinTime;
//	static int MinDist;
	
	static ArrayList<Integer> path;
	static int[] pathWeights;
	static int y_primal_bound;
	
	
	
	
	
	public PulseGraph( int numNodes) {
		super();
		C = new int[DataHandler.arc_weights];
		this.numNodes = numNodes;
		//nodeList = new Hashtable<Integer, VertexPulse>(numNodes);
		Cd=0;
		Ct=0;
		vertexes = new VertexPulse[numNodes];
		path= new ArrayList<Integer>();
		pathWeights= new int[DataHandler.arc_weights];
		y_primal_bound = -1;
		
	}
	
	public void resetPrimalSolution(){
		path= new ArrayList<Integer>();
		pathWeights= new int[DataHandler.arc_weights];
		y_primal_bound = -1;
	}
	@Override
	public EdgePulse addEdge(VertexPulse sourceVertex, VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public  int getNumNodes()
	{
		return numNodes;
	}
	public VertexPulse getVertexByID(int id){
		return vertexes[id];
	}
	
	public EdgePulse addWeightedEdge(VertexPulse sourceVertex, VertexPulse targetVertex,  int[] atris , int id) {
		for (int i = 0; i < DataHandler.arc_weights; i++) {
			if(atris[i]>C[i]){
				C[i]=atris[i];
			}
		}
		
		
		vertexes[targetVertex.getID()].addReversedEdge(new EdgePulse(targetVertex , sourceVertex, id, atris));
		vertexes[sourceVertex.getID()].magicIndex.add(id);
		return null;
	}
	
	
	@Override
	public boolean addEdge(VertexPulse sourceVertex, VertexPulse targetVertex, EdgePulse e) {
		return false;
	}

	@Override
	public boolean addVertex(VertexPulse v) {
		vertexes[v.getID()] = v;
		return true;
	}
	public boolean addFinalVertex(FinalVertexPulse v) {
		vertexes[v.getID()] = v;
		return true;
	}

	@Override
	public boolean containsEdge(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(EdgePulse e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsVertex(VertexPulse v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<EdgePulse> edgeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<EdgePulse> edgesOf(VertexPulse vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<EdgePulse> getAllEdges(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgePulse getEdge(VertexPulse sourceVertex, VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgeFactory<VertexPulse, EdgePulse> getEdgeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexPulse getEdgeSource(EdgePulse e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexPulse getEdgeTarget(EdgePulse e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getEdgeWeight(EdgePulse e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean removeAllEdges(Collection<? extends EdgePulse> edges) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<EdgePulse> removeAllEdges(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllVertices(Collection<? extends VertexPulse> vertices) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EdgePulse removeEdge(VertexPulse sourceVertex,
			VertexPulse targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEdge(EdgePulse e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeVertex(VertexPulse v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<VertexPulse> vertexSet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int getCd()
	{
		return Cd;
	}
	public int getCt()
	{
		return Ct;
	}
	
	public void resetNetwork1(){
		for (int i = 0; i < numNodes ; i++) {
			vertexes[i].reset();
		}
	}
	
	public ArrayList<Integer> getPaths()
	{
		return path;
	}
	

	public int getC(int obj) {
		return C[obj];
	}
	

}
