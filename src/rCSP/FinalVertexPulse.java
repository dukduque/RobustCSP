package rCSP;

import java.util.ArrayList;



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
	
	
	public void pulse(int[] pObjs, ArrayList<Integer> path) {
//		
//		System.out.println("Llego pulso");
		path.add(id);
		int y_path = 0;
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if (pObjs[i] <= DataHandler.b) {
				y_path++;
			}
		}
		if(!CheckDominance(pObjs) && y_path>=PulseGraph.y_primal_bound){
			PulseGraph.y_primal_bound = y_path;
			PulseGraph.path = new ArrayList<Integer>();
			PulseGraph.path.addAll(path);
			for (int i = 0; i < pObjs.length; i++) {
				PulseGraph.pathWeights[i]=pObjs[i];
			}
			
			//double data  = pg.calcDominatedArea();
			//System.out.println("porcentaje " + data );
			
//			System.out.println("Llego pulso con " +y_path+ "\t scenarios" + path );
//			for (int i = 0; i < pObjs.length; i++) {
//				System.out.print(pObjs[i]+" ");
//			}System.out.println();
			//System.out.println("llegooo otrooooooooo y contando: p   " + PulseGraph.Paths.size());
		}
		path.remove((path.size()-1));
	}

	private boolean CheckDominance(int[] objs) {
		return false;
	}

}
