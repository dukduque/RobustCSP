package RobCSP_Enum;

import java.util.ArrayList;

import umontreal.iro.lecuyer.probdist.NormalDist;



public class FinalVertexPulse extends VertexPulse{
	
	public int id;
	private EdgePulse reverseEdges;
	
		/**
	 * This matrix contains the sp bounds for each node M_ii correspond to the
	 * sp from the end node to this node of the objective i. M_ij correspond to
	 * the objective consumptions of the objective j when the objective i is
	 * minimum;
	 */
	public int[][] spMatrix;
	private PulseGraph pg;
	private boolean[] inserted;
	int c=0;
	int d=0;
	public FinalVertexPulse(int iD, PulseGraph npg) {
		super(iD);
		pg= npg;
		id = iD;
		spMatrix = new int[DataHandler.arc_weights][DataHandler.arc_weights];
		inserted = new boolean[DataHandler.arc_weights];
		for (int i = 0; i < DataHandler.arc_weights; i++) {
			spMatrix[i][i] = infinity;
			inserted[i] = false;
			//left[i] = this;
			//rigth[i] = this;
		}
		
	}

	
	public int  getID()
	{
		return id;
	}
	
	public void addReversedEdge(EdgePulse e)
	{
		if(reverseEdges!=null){
			reverseEdges.addNextCommonTailEdge(e);
		}else
			reverseEdges = e;
	}
	
	
	public EdgePulse findEdgeByTarget(VertexPulse target){
		if(reverseEdges!=null){
			reverseEdges.findEdgebyTarget(target);
		}
		return null;
	}
	public EdgePulse getReversedEdges() {
		return reverseEdges;
	}
	
	
	public void pulse(int y_bound, int[] pObjs, ArrayList<Integer> path, ArrayList<Integer> path_arcs) {

		path.add(id);
		PulseGraph.totalPaths++;
//		if (pObjs[DataHandler.scenarios] <= DataHandler.T) {
			PulseGraph.totalFeasiblePaths++;
			double mu = 0, var = 0;
			for (int j = 0; j < path_arcs.size(); j++) {
				int arc = path_arcs.get(j);
				mu += DataHandler.weights[arc][0];
				var += Math.pow(DataHandler.weights[arc][1],2);
			}
			double prob_b = NormalDist.cdf(mu, Math.sqrt(var), DataHandler.b);
			double prob_w = NormalDist.cdf(mu, Math.sqrt(var), DataHandler.w);
			pg.addSol(prob_b, prob_w , path, path_arcs,pObjs[DataHandler.scenarios] <= DataHandler.T);
//			System.out.println(prob_b + "  " + prob_w);
//		}
		path.remove((path.size() - 1));
	}

	private boolean CheckDominance(int[] objs) {
		return false;
	}

}
