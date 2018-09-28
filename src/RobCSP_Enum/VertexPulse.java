package RobCSP_Enum;

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

//	private int labels[][];

	boolean firstTime = true;

	private int usedLabels = 0;
	ArrayList<Label> labels;
	
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
		labels=new ArrayList<Label>();
//		labels = new int[DataHandler.numLabels][DataHandler.arc_weights];
//		for (int k = 0; k < DataHandler.numLabels; k++) {
//			for (int j = 0; j < DataHandler.arc_weights; j++) {
//				labels[k][j] = infinity;
//			}
//		}
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
//		for (int k = 0; k < DataHandler.numLabels; k++) {
//			for (int j = 0; j < DataHandler.arc_weights; j++) {
//				labels[k][j] = infinity;
//			}
//		}
		labels.clear();
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
	 * @param path_arcs 
	 */
	public void pulse(int y_primal_bound, int[] pulseWeights, ArrayList<Integer> path, ArrayList<Integer> path_arcs) {
		if (!path.contains(id)) {
			path.add(id);
			for (int i = 0; i < magicIndex.size(); i++) {
				path_arcs.add(magicIndex.get(i));
				int head_node = DataHandler.Arcs[magicIndex.get(i)][1];
				int[] newWeights = new int[DataHandler.arc_weights];
				for (int j = 0; j < newWeights.length; j++) {
					newWeights[j] = pulseWeights[j] + DataHandler.weights[magicIndex.get(i)][j];
				}
				int new_primal_bound = calcPrimalBound(newWeights, head_node);
				PulseGraph.vertexes[head_node].pulse(new_primal_bound, newWeights, path, path_arcs);
				path_arcs.remove(path_arcs.size() - 1);
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
	 * Checks for w-robustness
	 * @param pWeights all the weights traveling in the pulse recursion.
	 * @param node Id of the node where the pruning strategy is been asked
	 * @return true if the pulse must be pruned, false otherwise.
	 */
	private boolean check_w_rob(int[] pWeights, int node) {
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if(pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] > DataHandler.w ){
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
		if (y_dual_bound <= PulseGraph.y_primal_bound) {
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
		int b_dualBound = 0;

		for (int i = 0; i < DataHandler.scenarios; i++) {
			if (pWeights[i] + PulseGraph.vertexes[node].spMatrix[i][i] <= DataHandler.b) {
				b_dualBound++;
			}
		}
		return b_dualBound;
	}
	
	public int calcPrimalBound(int[] pWeights, int node) {
		int b_dualBound = 0;
		for (int i = 0; i < DataHandler.scenarios; i++) {
			if (pWeights[i] <= DataHandler.b) {
				b_dualBound++;
			}
		}
		return b_dualBound;
	}
	
	public boolean CheckLabels(int[] pulse_weights, int node) {
		// TODO
		for (int i = 0; i < PulseGraph.vertexes[node].labels.size(); i++) {
			if (PulseGraph.vertexes[node].labels.get(i).dominateLabel(pulse_weights)) {
				return true;
			}
		}
		return false;
		
//		for (int i = 0; i < this.usedLabels; i++) {
//			
//			int domObjs = 0;
//
//			for (int j = 0; j < pulse_weights.length; j++) {
//				if (pulse_weights[j] >= PulseGraph.vertexes[node].labels[i][j]) {
//					domObjs++;
//				} else {
//					j = pulse_weights.length + 100;
//				}
//			}
//			if (domObjs == pulse_weights.length) {
//				return true;
//				
//			}
//
//		}
//		return false;
	}

	private void changeLabels(int[] objs) {
		if (labels.size() == 0) {
			labels.add(new Label(objs));
		} else if (labels.size() == 1) {
			labels.add(objs[0] < labels.get(0).attributes[0] ? 0 : 1, new Label(objs));
		} else {
			if (labels.size() < DataHandler.numLabels) {
				insertLabel(objs);
			} else {
				int luck = 1 + DataHandler.r.nextInt(DataHandler.numLabels - 2);
				labels.remove(luck);
				insertLabel(objs);
			}
			
		}
		
//		for (int j = 0; j < objs.length; j++) {
//			if(objs[j]<= labels[j][j]){
//				if(labels[j][j]==infinity){
//					this.usedLabels++;
//				}
//				for (int jk = 0; jk < objs.length; jk++) {
//					labels[j][jk] = objs[jk];
//				}
//				
//				return;
//			}
//		}
//		
//		if(this.usedLabels<DataHandler.numLabels){
//			for (int j = 0; j < objs.length; j++) {
//				labels[usedLabels][j] = objs[j];
//			}
//			usedLabels++;
//			
//		}else{
//			int luck = objs.length + DataHandler.r.nextInt(DataHandler.numLabels-objs.length );
//			for (int j = 0; j < objs.length; j++) {
//				labels[luck][j] = objs[j];
//			}
//		}
		
	}

	private void insertLabel(int[] objs) {
		Label np = new Label(objs);
		double cScore = np.attributes[0];
		
		boolean cond = true;
		int l = 0;
		int r = labels.size();
		int m = (int) ((l + r) / 2);
		double mVal = 0;
		if (labels.size() == 0) {
			labels.add(np);
			return;
		} else if (labels.size() == 1) {
			mVal = labels.get(m).attributes[0];;
			labels.add(cScore < mVal ? 0 : 1, np);
			return;
		} else {
			mVal = labels.get(m).attributes[0];
		}

		while (cond) {
			if (r - l > 1) {
				if (cScore < mVal) {
					r = m;
					m = (int) ((l + r) / 2);
				} else if (cScore > mVal) {
					l = m;
					m = (int) ((l + r) / 2);
				} else {
					labels.set(m, np);
					return;
				}
				mVal = labels.get(m).attributes[0];;
			} else {
				cond = false;
				if (l == m) {
					labels.add(cScore < mVal ? l : l + 1, np);
				} else if (r == m) {
					System.out.println("esto no pasa ");
					labels.add(cScore < mVal ? r : Math.min(r + 1, labels.size()), np);
				} else {
					System.err.println(VertexPulse.class +  " insert label, error");
				}
				return;

			}
		}
		
	}

	private void Sort(ArrayList<Integer> set) {
		QS(magicIndex, 0, magicIndex.size() - 1);
	}

	public int colocar(ArrayList<Integer> e, int b, int t) {
		int i;
		int pivote;
		int valor_pivote;
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
