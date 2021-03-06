package MinMax_RobCSP_Pulse;

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


	public void reset() {
		usedLabels = 0;
		for (int k = 0; k < DataHandler.numLabels; k++) {
				for (int j = 0; j < DataHandler.arc_weights; j++) {
					labels[k][j] = infinity;
				}
			}
	}

	/**
	 * Pulse algorithm function
	 * 
	 * @param y_dual_bound
	 *            is the the objective function that tells the number of
	 *            scenarios in which the partial path exhibits less than b of
	 *            the bw-robustness criterion
	 * @param pulseWeights
	 *            weights of all scenarios and resource constraints traveling in
	 *            the recursion
	 * @param path
	 *            Current path (ordered set of nodes) that is being explored
	 */
	public void pulse(int y_primal_bound, int[] pulseWeights, ArrayList<Integer> path) {
		if (this.firstTime) {
			this.firstTime = false;
			this.Sort(this.magicIndex);
		}
		changeLabels(pulseWeights);
		if (!path.contains(id)) {
			path.add(id);
			for (int i = 0; i < magicIndex.size(); i++) {
				int head_node = DataHandler.Arcs[magicIndex.get(i)][1];
				int[] newWeights = new int[DataHandler.arc_weights];
				for (int j = 0; j < newWeights.length; j++) {
					newWeights[j] = pulseWeights[j] + DataHandler.weights[magicIndex.get(i)][j];
				}
				if (!checkInfeasibility(newWeights, head_node)) {
					int new_dual_bound = calcDualBound(newWeights, head_node);
					int new_primal_bound = calcPrimalBound(newWeights, head_node);
					if(!check_b_rob(new_dual_bound) ) {
						if (!CheckLabels(newWeights, head_node)) {
							PulseGraph.vertexes[head_node].pulse(new_primal_bound, newWeights, path);
						}
					}

				}
			}
			path.remove((path.size() - 1));
		}
	}



	/**
	 * Checks for infeasibility of the resource constraint
	 * @param pWeights all the weights traveling in the pulse recursion.
	 * @param node Id of the node where the pruning strategy is been asked
	 * @return true if the pulse must be pruned, false otherwise.
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
	 * Checks for b_roustness given a pre-calculated bound
	 * @param pWeights all the weights traveling in the pulse recursion.
	 * @param node Id of the node where the pruning strategy is been asked
	 * @return true if the pulse must be pruned, false otherwise.
	 */
	private boolean check_b_rob(int y_dual_bound) {
		if (y_dual_bound >= PulseGraph.z_down) {
			return true;
		}
		return false;
	}

	/**
	 * Checks for b_roustness
	 * @param pWeights all the weights traveling in the pulse recursion.
	 * @param node Id of the node where the pruning strategy is been asked
	 * @return Best bound for y_bar.
	 */
	public int calcDualBound(int[] pWeights, int node) {
		int z_dualBound = 0;

		for (int i = 0; i < DataHandler.scenarios; i++) {
			if (pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] >= z_dualBound) {
				z_dualBound = pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i];
			}
		}
		return z_dualBound;
	}
	
	public int calcPrimalBound(int[] pWeights, int node) {
		int z_max_path = 0;
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if (pWeights[i] >= z_max_path) {
				z_max_path=pWeights[i];
			}
		}
		return z_max_path;
	}
	
	public boolean CheckLabels(int[] pulse_weights, int node) {
		// TODO
		for (int i = 0; i < this.usedLabels; i++) {
			
			int domObjs = 0;

			for (int j = 0; j < pulse_weights.length; j++) {
				if (pulse_weights[j] >= PulseGraph.vertexes[node].labels[i][j]) {
					domObjs++;
				} else {
					j = pulse_weights.length + 100;
				}
			}
			if (domObjs == pulse_weights.length) {
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
		valor_pivote = PulseGraph.vertexes[DataHandler.Arcs[e.get(pivote)][1]].getCompareCriteria();
		for (i = b + 1; i <= t; i++) {
			if (PulseGraph.vertexes[DataHandler.Arcs[e.get(i)][1]].getCompareCriteria() < valor_pivote) {
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
