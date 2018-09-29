package bwRobSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

public class DataHandler {
	
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
	static int Source;
	static int[][] Arcs;
	static int NumNodes;
	
	/**
	 * For each arc, there is one cost per scenario
	 */
	static int[][] weights;
	

	private PulseGraph Gd;
	
	/*
	 * Max cost
	 */
	public static int w;
	/*
	 * Target cost
	 */
	public static int b;
	
	
	static Random r = new Random(0);
	private int seed;
	public static int numLabels;
	
	
	
	public DataHandler(Settings Instance, int n_scenarios, int numCtrs ) {
		scenarios = n_scenarios;
		constraints = numCtrs;
		arc_weights = scenarios + constraints;
		numLabels = arc_weights*2;//Default was 2
		CvsInput = Instance.DataFile;
		NumArcs = Instance.NumArcs;
		NumNodes = Instance.NumNodes;
		LastNode = Instance.sink;
		Source = Instance.source;
		
				
		seed = Instance.seed;//System.out.println(seed+"sedd");
		Arcs = new int[Instance.NumArcs][2];
		weights = new int[Instance.NumArcs][arc_weights];
		weight_bound_availability = new int[arc_weights];
		Gd = new PulseGraph(NumNodes);
	}

	public void ReadC() throws NumberFormatException, IOException {
		Random weithgen = new Random(seed);
		
		File file = new File(CvsInput);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		String[] readed = new String[arc_weights+2*100];

		int row = 0;
		int col = 0;
		
		upLoadNodes();
		int fileStep = 1;
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
				Gd.addWeightedEdge( Gd.getVertexByID(Arcs[row - fileStep][0]), Gd.getVertexByID(Arcs[row - fileStep][1]),atris , row-fileStep);
			}

			col = 0;
			row++;

		}

	
	}
	public void upLoadNodes(){
		for (int i = 0; i < NumNodes; i++) {
			if(i!=(LastNode-1)){
				Gd.addVertex(new VertexPulse(i) );
			}
		}
		FinalVertexPulse vv = new FinalVertexPulse(LastNode-1, Gd);
		Gd.addFinalVertex(vv);
	}
	
	public PulseGraph getGd()
	{
		return Gd;
	}
	
	public void calcMin() {
		min_sp_of_scenarios=Integer.MAX_VALUE;
		int[] source_sp = PulseGraph.vertexes[Source-1].spVector;
		for (int i = 0; i < scenarios; i++) {
			if(source_sp[i]<min_sp_of_scenarios){
				min_sp_of_scenarios = source_sp[i];
			}
		}
//		System.out.println(min_sp_of_scenarios);
	}

	public void calMax() {
		max_sp_of_scenarios = 0;
		int[] source_sp = PulseGraph.vertexes[Source-1].spVector;
		for (int i = 0; i < scenarios; i++) {
				max_sp_of_scenarios += source_sp[i];
		}
//		System.out.println(max_sp_of_scenarios);
	}
	
	public void set_w(double alpha) {
		w = (int) (min_sp_of_scenarios + alpha*(max_sp_of_scenarios-min_sp_of_scenarios));
		System.out.print("/"+alpha);
	}
	
	public void set_b(double beta){
		b = (int) (min_sp_of_scenarios + beta*(w-min_sp_of_scenarios));
		System.out.print("/"+beta);
	}
}
