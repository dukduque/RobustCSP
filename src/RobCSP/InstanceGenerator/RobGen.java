package RobCSP.InstanceGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.UniformDist;
import umontreal.iro.lecuyer.probdist.UniformIntDist;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class RobGen {

	private int[] Distance = null;
	private int[] Time = null;
	private String DataFile;
	private int NumArcs;
	private int LastNode;
	private int Source;
	private  int[][] Arcs;
	static int[][] weigths;
	static int NumNodes;
	static Random rr;
	private RandomStream r = new MRG32k3a();
	int sce = 0;
	
	public RobGen(int total_weights, String ini, String variation, String filename, int numInstances ) throws IOException {
		gen2(total_weights, ini, variation, filename, numInstances);		
	}	
	
	private void gen2(int total_weights, String ini, String variation, String file, int numInstances) throws IOException{
		
		sce = total_weights-1;
		setUp(ini);
		Arcs = new int[NumArcs][2];
		Distance= new int[NumArcs];
		Time = new int[NumArcs];
		weigths = new int[NumArcs][total_weights];
		ReadC();
		Random random_mean  = new Random(0);
		Random random_amplitud  = new Random(1);
		
		r.resetStartStream();
		for (int i = 0; i < NumArcs; i++) {
			Random random_gen = new Random(i);
			int arc_mean = UniformIntDist.inverseF(1000, 3000, random_mean.nextDouble());
			int arc_shape = UniformIntDist.inverseF(1, 3, random_amplitud.nextDouble()); 
			for (int k = 0; k < sce; k ++) {
//				weigths[i][k]=Math.max(0,(int)NormalDist.inverseF(Distance[i], Math.max(Distance[i],1)/4.0, r.nextDouble()));
				if(sce == 1){
//					weigths[i][k]=arc_mean*100;
					weigths[i][k]=arc_mean;
				}else{
					double lambda = (arc_shape+0.0)/(arc_mean);
					weigths[i][k]=(int)GammaDist.inverseF(arc_shape, lambda , 1, random_gen.nextDouble());
//					weigths[i][k]=UniformIntDist.inverseF(arc_mean-arc_widht, arc_mean+arc_widht, r.nextDouble());
//					weigths[i][k]=(int) Math.round(100*ExponentialDist.inverseF(1.0/arc_mean, r.nextDouble()));
				}
			}
			weigths[i][sce] = arc_shape;//Time[i];
		}
		write(sce, ini, file, numInstances);
	}
	
	private void write(int objectives, String ini, String filename, int numInstances) throws IOException {
		String in = ini.replaceAll(".ini", "");
		in = in.replaceAll("ini/", "");
		String path ="data/"+gen+"_"+ objectives +".txt";
		
		//Write netwrok
		File file = new File(path);
		PrintWriter bufRdr = new PrintWriter(file);
		String line = null;
		line = "sp min " + NumNodes + " " + NumArcs;line+="\n";
		bufRdr.write(line);
//		line = Source + " " + 1;line+="\n";
//		bufRdr.write(line);
//		line = LastNode + " " + "-1";line+="\n";
//		bufRdr.write(line);
		
		for (int i = 0; i < NumArcs; i++) {
			line = Arcs[i][0] + " " +   Arcs[i][1] ;
			for (int j = 0; j < weigths[i].length; j++) {
				line += " " + weigths[i][j];
			}
			line+="\n";
//			System.out.print(line);
			bufRdr.write(line);
		}
		bufRdr.close();

		for (int i = 0; i < numInstances; i++) {
			if (sources[i] == 0) {
				sources[i] = 1 + rr.nextInt(NumNodes - 1);
				sinks[i] = 1 + rr.nextInt(NumNodes - 1);
			}
			if (numInstances == 1) {
				sources[i] = 1;
				sinks[i] = NumNodes;
			}
			writeIni(i, path, filename, NumArcs, NumNodes, 0, objectives,
					sources[i], sinks[i]);
		}

	}

	private void writeIni(int intance, String path,String originalFile, int numArcs2, int numNodes2, int tc, int scenarios, int source2, int lastNode2) throws FileNotFoundException {
		int num = intance+1;
		File file = new File("./ini/"+ gen +(num)+"_"+sce+ ".ini");
		
		
		PrintWriter bufRdr = new PrintWriter(file);
		String line = "DataFile:"+path+System.getProperty("line.separator");
		line += "Number of Arcs:"+numArcs2+System.getProperty("line.separator");
		line += "Number of Nodes:"+numNodes2+System.getProperty("line.separator");
		line += "Time Constraint:"+tc+System.getProperty("line.separator");
		line += "Number of Scenarios:"+scenarios+System.getProperty("line.separator");
		line += "Source:"+source2+System.getProperty("line.separator");
		line += "Last Node:"+lastNode2+System.getProperty("line.separator");
		bufRdr.write(line);
		
		bufRdr.close();

		
	}

	public void ReadC() throws NumberFormatException, IOException {

		//File file = new File(p.getParent().getParent().getParent().getParent() + "/workspace/Proyectos BSP/"+ DataFile);
		File file = new File(p.getParent().getParent() + "/ProyectosBSP/"+ DataFile);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		String[] readed = new String[30];

		int row = 0;
		int col = 0;
		
		int lineStep = 3;
		
		while ((line = bufRdr.readLine()) != null && row < NumArcs + lineStep) {
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				readed[col] = st.nextToken();
				col++;
			}

			if (row >= lineStep) {
				Arcs[row - lineStep][0] = (Integer.parseInt(readed[0]));
				Arcs[row - lineStep][1] = (Integer.parseInt(readed[1]));
				Distance[row - lineStep] = Integer.parseInt(readed[2]);
				Time[row - lineStep] = Integer.parseInt(readed[3]);
				//Gd.addWeightedEdge( Gd.getVertexByID(Arcs[row - 3][0]), Gd.getVertexByID(Arcs[row - 3][1]),Distance[row - 3], Time[row - 3] , row-3);
			}

			col = 0;
			row++;

		}

	
	}
	
	
	public void setUp(String ConfigFile) throws IOException{
		
		File file = new File(ConfigFile);
		
		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
		String line = null;
		
		String [][] readed = new String [5][2];
		
		int row = 0;
		int col = 0;
	 
		// read each line of text file
		while ((line = bufRdr.readLine()) != null && row < 5) {
			StringTokenizer st = new StringTokenizer(line, ":");
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				readed[row][col] = st.nextToken();
				col++;

			}
			col = 0;
			row++;

		}

		DataFile = readed[0][1];
		NumArcs = Integer.parseInt(readed[1][1]);
		NumNodes = Integer.parseInt(readed[2][1]);
		Source = Integer.parseInt(readed[3][1]);
		LastNode = Integer.parseInt(readed[4][1]);
		
		
		
		
	}
	
	
	public static Path p = Paths.get(System.getProperty("user.dir"));
	public static String gen = "DC";//"rcsp"; //5 ,7 , 13 ,15 21, 23 
	public static int numIns = 30;//1;
	public static int[] sources = new int[numIns];
	public static int[] sinks = new int[numIns];
	public static void main(String[] args) {
		try {
			int[] numScenarios = {1,10, 50, 100, 200, 500, 1000};//, 10000}; 
			rr = new Random(0);
			int [] RCSPGEN = {1};//{5 ,7 , 13 ,15, 21, 23};
			for (int r = 0; r < RCSPGEN.length; r++) {
				for (int i = 0; i <numScenarios.length; i++) {
//						gen = "rcsp" + RCSPGEN[r];
						//gen = "RI1";
						String str_varr ="gamma";
//						String ini = p.getParent().getParent().getParent().getParent() + "/workspace/Proyectos BSP/ini/RCSP/"+gen+"_3_1"+".ini";
						//String ini = p.getParent().getParent().getParent().getParent() + "/workspace/ProyectosBSP/ini/"+gen+".ini";
						String ini = p.getParent().getParent()+ "/ProyectosBSP/ini/"+gen+1+".ini";
						
						System.out.println(ini);
						//gen = "rcsp" + RCSPGEN[r] + str_varr;
						//gen = "RI";
						RobGen nm = new RobGen(numScenarios[i]+1, ini, str_varr, (gen+str_varr), numIns);
						System.out.println("salen "  + numIns  + "  " + gen + " con " + numScenarios[i] +"escenarios");
			}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
