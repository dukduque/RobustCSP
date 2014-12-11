package rCSP;

import java.util.ArrayList;

public class VertexPulse {
	
	public static final int infinity = (int) Double.POSITIVE_INFINITY;

	private EdgePulse reverseEdges;
	ArrayList<Integer> magicIndex;
	public int id;
	
	public VertexPulse[] left;
	public VertexPulse[] rigth;

	/**
	 * This matrix contains the sp bounds for each node M_ii correspond to the
	 * sp from the end node to this node of the objective i. M_ij correspond to
	 * the objective consumptions of the objective j when the objective i is
	 * minimum;
	 */
	public int[][] spMatrix;

	public boolean[] inserted;
	/**
	 * PULSE
	 */

	private int labels[][];

	boolean firstTime = true;

	private int usedLabels = 0;

	public VertexPulse(int iD) {
		id = iD;
		spMatrix = new int[DataHandler.arc_weights][DataHandler.arc_weights];
		left  = new  VertexPulse[DataHandler.arc_weights];
		rigth = new  VertexPulse[DataHandler.arc_weights];
		inserted = new boolean[DataHandler.arc_weights];
		for (int i = 0; i < DataHandler.arc_weights; i++) {
			spMatrix[i][i] = infinity;
			inserted[i] = false;
			left[i] = this;
			rigth[i] = this;
		}

		labels = new int[DataHandler.numLabels][DataHandler.arc_weights];
		for (int k = 0; k < DataHandler.numLabels; k++) {
			for (int j = 0; j < DataHandler.arc_weights; j++) {
				labels[k][j] = infinity;
			}
		}
		magicIndex = new ArrayList<Integer>();
	}

	public int getID() {
		return id;
	}

	public void addReversedEdge(EdgePulse e) {
		if (reverseEdges != null) {
			reverseEdges.addNextCommonTailEdge(e);
		} else
			reverseEdges = e;
	}

	public EdgePulse getReversedEdges() {
		return reverseEdges;
	}

	/**
	 * Sets dijkstra's shortest path label on the objective specified
	 * 
	 * @param obj
	 *            objective of the shortest path
	 * @param c
	 *            label of the objective obj
	 */
/*	public void setMinObjective(int obj, int c) {
		spMatrix[obj][obj] = c;
	}

	public int getDualBound(int obj) {
		return spMatrix[obj][obj];
	}

	public void setMaxObjectives(int obj, int maxObj, int c) {
		spMatrix[obj][maxObj] = c;
	}

	public int getObjectiveLabel(int obj, int maxObj) {
		return spMatrix[obj][maxObj];
	}
	public int getMaxObjLabel(int obj, int maxObj) {
		return spMatrix[obj][maxObj];
	}
*/

	/**
	 * Unlink a vertex from the bucket
	 * 
	 * @return true, if the buckets gets empty
	 */
	/*public boolean unLinkVertex(int obj) {
		if (rigth[obj].getID() == id) {
			left[obj] = this;
			rigth[obj] = this;
			return true;
		} else {
			left[obj].rigth[obj] =rigth[obj];
			rigth[obj].left[obj] = left[obj];
			left[obj] = this;
			rigth[obj] = this;
			return false;
		}
	}
*/

	/*public void fastUnlink(int obj) {
		left[obj] = this;
		rigth[obj] = this;
	}
*/
	/*public void unlinkRighBound(int obj) {
		rigth[obj] = null;
	}*/

	/**
	 * Insert a vertex in a bucket. New vertex is inserted on the left of the
	 * bucket entrance
	 * 
	 * @param vertex in progress to be inserted 
	 * @param obj objective been optimized
	 */
	/*public void insertVertex(int obj, VertexPulse v) {
		v.setLeft(obj, left[obj]);
		v.setRigth(obj, this);
		left[obj].setRigth(obj, v);
		left[obj] = v;
	}*/


	/*public void setRigth(int obj, VertexPulse v) {
		rigth[obj] = v;
	}

	public void setLeft(int obj, VertexPulse v) {
		left[obj] = v;
	}*/

	/*public VertexPulse getRigth(int obj) {
		return rigth[obj];
	}

	public VertexPulse getLeft(int obj) {
		return left[obj];
	}
*/

	/*public void setInserted( int obj ) {
		inserted[obj] = true;
	}
	
	public boolean isInserted(int obj) {
		return inserted[obj];
	}

*/
	public void reset() {
		for (int i = 0; i < DataHandler.arc_weights; i++) {
			inserted[i] = false;
		}
	}

	/*
	 * public void setBounds(int MT, int MD){ maxDist = MD- minDist; maxTime =
	 * MT - minTime; bLeft = null; bRigth = null; reverseEdges = null; }
	 */

	public void pulse(int [] pulseWeights,  ArrayList<Integer> path) {
			if (this.firstTime) {
			this.firstTime = false;
			this.Sort(this.magicIndex);
		/**	for (int i = 0; i < DataHandler.objectives; i++) {
				left[i] = null;
				rigth[i] = null;
			}
			reverseEdges = null;*/
		}
		//System.out.println(id+ "  ///////" + PulseGraph.Paths.size());
		changeLabels(pulseWeights);
		if (!path.contains(id)) {
			path.add(id);
			for (int i = 0; i < magicIndex.size(); i++) {
				int head_node = DataHandler.Arcs[magicIndex.get(i)][1];
				int[] newWeights = new int[DataHandler.arc_weights]; 
				for (int j = 0; j < newWeights.length; j++) {
					newWeights[j] = pulseWeights[j] + DataHandler.weights[magicIndex.get(i)][j];
				}
				// && !CheckLabels(newWeights, head_node)
				
				if (!checkInfeasibility(newWeights, head_node) && !check_w_rob(newWeights, head_node) && !Check_b_rob(newWeights, head_node)) {
					PulseGraph.vertexes[head_node].pulse(newWeights, path);
				}
			}
			path.remove((path.size() - 1));
		}
	}

	/**
	 * Checks for infeasibility of the resource constraint
	 * @param pWeights
	 * @param node
	 * @return
	 */
	private boolean checkInfeasibility(int[] pWeights, int node) {
		for (int i = DataHandler.scenarios; i < pWeights.length; i++) {
			if(pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] > DataHandler.T ){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for w-robustness
	 * @param pWeights all cumulative weights (scenarios + constrains)
	 * @param node 
	 * @return
	 */
	private boolean check_w_rob(int[] pWeights, int node) {
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if(pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] > DataHandler.W ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks for b_roustness
	 * @param pWeights
	 * @param node
	 * @return
	 */
	private boolean Check_b_rob(int[] pWeights,  int node) {
		int b_dualBound = 0;
		
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if(pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] <= DataHandler.b ){
				b_dualBound ++;
			}
		}
		if(b_dualBound<=PulseGraph.y_primal_bound)
		{
			return true;
		}
		return false;
	}

	public boolean CheckLabels(int[] objs, int node) {
		// TODO
		for (int i = 0; i < this.usedLabels; i++) {
			
			int domObjs = 0;

			for (int j = 0; j < objs.length; j++) {
				if (objs[j] >= PulseGraph.vertexes[node].labels[i][j]) {
					domObjs++;
				} else {
					j = objs.length + 10;
				}
			}
			if (domObjs == objs.length) {
				return true;
				
			}

		}
		return false;
	}

	private void changeLabels(int[] objs) {
		for (int j = 0; j < objs.length; j++) {
			if(objs[j]<= labels[j][j]){
				if(labels[j][j]==infinity){
					this.usedLabels++;
				}
				for (int jk = 0; jk < objs.length; jk++) {
					labels[j][jk] = objs[jk];
				}
				
				return;
			}
		}
		
		if(this.usedLabels<DataHandler.numLabels){
			for (int j = 0; j < objs.length; j++) {
				labels[usedLabels][j] = objs[j];
			}
			usedLabels++;
			
		}else{
			int luck = objs.length + DataHandler.r.nextInt(DataHandler.numLabels-objs.length );
			for (int j = 0; j < objs.length; j++) {
				labels[luck][j] = objs[j];
			}
		}
		
	}

	private void Sort(ArrayList<Integer> set) {
		QS(magicIndex, 0, magicIndex.size() - 1);
	}

	public int colocar(ArrayList<Integer> e, int b, int t) {
		int i;
		int pivote, valor_pivote;
		int temp;

		pivote = b;
		valor_pivote = PulseGraph.vertexes[DataHandler.Arcs[e.get(pivote)][1]]
				.getCompareCriteria();
		for (i = b + 1; i <= t; i++) {
			if (PulseGraph.vertexes[DataHandler.Arcs[e.get(i)][1]]
					.getCompareCriteria() < valor_pivote) {
				pivote++;
				temp = e.get(i);
				e.set(i, e.get(pivote));
				e.set(pivote, temp);
			}
		}
		temp = e.get(b);
		e.set(b, e.get(pivote));
		e.set(pivote, temp);
		return pivote;
	}

	public void QS(ArrayList<Integer> e, int b, int t) {
		int pivote;
		if (b < t) {
			pivote = colocar(e, b, t);
			QS(e, b, pivote - 1);
			QS(e, pivote + 1, t);
		}
	}

	
	public int getCompareCriteria() {
		int suma = 0;
		for (int j = 0; j <  DataHandler.arc_weights; j++) {
			suma += this.spMatrix[j][j];
		}
		return suma;
	}

	
	public String toString(){
		return ""+ id;
	}

	
}
