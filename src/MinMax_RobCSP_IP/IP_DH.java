package MinMax_RobCSP_IP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class IP_DH {
	
	/**
	 * Constant that stays the number of objectives
	 */
	public static int arc_weights;
	
	
	/**
	 * availability 
	 */
	public static int[] weight_bound_availability;
	
	public static int scenarios;
	public static int constraints;
	
	public static int min_sp_of_scenarios;
	public static int max_sp_of_scenarios;
	
	
	String CvsInput;
	int NumArcs;
	int LastNode;
	int Source;
	static int[][] Arcs;
	static int NumNodes;
	
	/**
	 * For each arc, there is one cost per scenario
	 */
	static int[][] weights;
	

//	public ArrayList<int[]> fwd_star;
//	public int[] fwd_pointers;
//	
	public ArrayList<Integer>[] fwd_arcs;
	public ArrayList<Integer>[] bwd_arcs;
	
	
	/**
	 * Maximum resource consumption
	 */
	public static int T;
	
	public static int w;
	
	public static int b;
	
	
	static Random r = new Random(0);
	private int seed;
	public static int numLabels;
	
	
	
	public IP_DH(Settings Instance, int n_scenarios, int numCtrs ) {
		scenarios = n_scenarios;
		constraints = numCtrs;
		arc_weights = scenarios + constraints;
		numLabels = arc_weights*2;
		CvsInput = Instance.DataFile;
		NumArcs = Instance.NumArcs;
		NumNodes = Instance.NumNodes;
		LastNode = Instance.sink;
		Source = Instance.source;
		T = Instance.Tmax;
				
		seed = Instance.seed;//System.out.println(seed+"sedd");
		Arcs = new int[Instance.NumArcs][2];
		weights = new int[Instance.NumArcs][arc_weights];
		weight_bound_availability = new int[arc_weights];
		
	}

	public void ReadC() throws NumberFormatException, IOException {
		
		//Random weithgen = new Random(seed);
		File file = new File(CvsInput);
		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		String[] readed = new String[arc_weights + 200];

		int row = 0;
		int col = 0;
		
		upLoadNodes();
		int fileStep = 3;
		while ((line = bufRdr.readLine()) != null && row < NumArcs + fileStep) {
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				readed[col] = st.nextToken();
				col++;
			}

			if (row >= fileStep) {
				Arcs[row - fileStep][0] = (Integer.parseInt(readed[0]) - 1);
				Arcs[row - fileStep][1] = (Integer.parseInt(readed[1]) - 1); 
				int[] atris = new int[arc_weights];
				for (int i = 0; i < arc_weights; i++) {
					atris[i] = Integer.parseInt(readed[2 + i]);
					weights[row - fileStep][i] = atris[i];
				}
				addArc(Arcs[row - fileStep][0],Arcs[row - fileStep][1]  , row - fileStep);
			}

			col = 0;
			row++;

		}
	
	
	}
	

	private void addArc(int v_i, int v_j, int arc_index) {
//		int point = fwd_pointers[v_i];
//		fwd_star.add(point, atris);
//		for (int i = v_i+1; i < fwd_pointers.length; i++) {
//			fwd_pointers[i]++;
//		}
		fwd_arcs[v_i].add(arc_index);
		bwd_arcs[v_j].add(arc_index);
	}

	public void upLoadNodes(){
//		fwd_pointers = new int[NumNodes+1];
//		fwd_star = new ArrayList<int[]>();
		fwd_arcs = new ArrayList[NumNodes];
		bwd_arcs = new ArrayList[NumNodes];
		for (int i = 0; i <NumNodes; i++) {
			bwd_arcs[i] = new ArrayList<Integer>();
			fwd_arcs[i] = new ArrayList<Integer>();
		}
		Source = Source - 1;
		LastNode = LastNode - 1;
	}
	

	public void set_w(double alpha) {
		
	}
	
	public void set_b(double beta){
		
	}
			
	public void setTmax(double gamma) {
		
	}
}
