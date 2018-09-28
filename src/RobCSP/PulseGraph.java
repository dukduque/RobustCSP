package RobCSP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import umontreal.iro.lecuyer.probdist.ErlangDist;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.UniformIntDist;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

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
	static ArrayList<Integer> path_arcs;
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
		path_arcs = new ArrayList<Integer>();
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

	public void a_Posteriori_evaluation() {
		
		RandomStream r_sim = new MRG32k3a();
		int counter_b = 0, counter_w = 0;
		int simulations = 10000;
		double mu_path = 0, var_path = 0;
		for (int i = 0; i < simulations; i++) {
			int cost = 0;
			for (int j = 0; j < path_arcs.size(); j++) {
				int arc = path_arcs.get(j);
				double lambda = 1.0 / DataHandler.weights[arc][0];
				double mu = DataHandler.weights[arc][0];
				if(i==0){
				mu_path += mu;
				var_path += Math.pow(mu/2.0, 2);}
				// cost += ExponentialDist.inverseF(lambda, r_sim.nextDouble());
//				cost += NormalDist.inverseF(mu, mu / 4, r_sim.nextDouble());
				cost += UniformIntDist.inverseF(1, 20, r_sim.nextDouble());
			}
			if (cost <= DataHandler.b) {
				counter_b++;
			}
			if (cost <= DataHandler.w) {
				counter_w++;
			}
		}
		if(path_arcs.size()==0){
			mu_path = 0;
			var_path = 0.01;
		}
		System.out.print("p{b}/" + counter_b / (0.0 + simulations));
//		System.out.println(mu_path + " , " + var_path);
//		System.out.print("/p{b*}/" + NormalDist.cdf(mu_path, Math.sqrt(var_path), DataHandler.b));
		System.out.print("/p{W}/" + counter_w / (0.0 + simulations));
//		System.out.print("/p{W*}/" + NormalDist.cdf(mu_path, Math.sqrt(var_path), DataHandler.w));
	}
	

}
