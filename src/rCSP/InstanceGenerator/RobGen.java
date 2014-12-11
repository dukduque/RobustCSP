package rCSP.InstanceGenerator;

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
	int[] lower = {1,67,134,199,265,331,397,463,529,595};
	//int[] lower = {100,300,500,700,900,1100,1300,1500,1700,1900};
	//int[] lower = {1000,3000,5000,7000,9000,11000,13000,15000,17000,19000};
	int[] upper = {32,99,165,231,297,363,429,495,561,627};
	static Random rr;;
	int sce = 0;
	
	public RobGen(int fo_scenarios, String ini, String variation, String filename ) throws IOException {
		gen2(fo_scenarios, ini, variation, filename);
	}	
	
	private void gen2(int fo_scenartios, String ini, String variation, String file) throws IOException{
		
		sce = fo_scenartios-1;
		setUp(ini);
		Arcs = new int[NumArcs][2];
		Distance= new int[NumArcs];
		Time = new int[NumArcs];
		weigths = new int[NumArcs][fo_scenartios];
		ReadC();
		
		for (int i = 0; i < NumArcs; i++) {
			for (int k = 0; k < sce; k ++) {
				weigths[i][k] = (int)Math.abs((Distance[i] + rr.nextGaussian()*(Distance[i]/4.0)));
//				weigths[i][k] = 1;
			}
			weigths[i][sce] = Time[i];
		}

		write(sce, ini, file);
	}
	
	private void write(int objectives, String ini, String filename) throws IOException {
		String in = ini.replaceAll(".ini", "");
		in = in.replaceAll("ini/", "");
		String path ="data/Rob_RoadNetworks/Rob_"+objectives+"_"+ filename +".txt";
		writeIni(path, filename, NumArcs, NumNodes, 5000 , objectives, Source, LastNode);
		
		File file = new File(path);
		PrintWriter bufRdr = new PrintWriter(file);
		String line = null;
		line = "sp min " + NumNodes + " " + NumArcs;line+="\n";
		bufRdr.write(line);
		line = Source + " " + 1;line+="\n";
		bufRdr.write(line);
		line = LastNode + " " + "-1";line+="\n";
		bufRdr.write(line);
		for (int i = 0; i < NumArcs; i++) {
			line = Arcs[i][0] + " " +   Arcs[i][1] ;
			for (int j = 0; j < weigths[i].length; j++) {
				line += " " + weigths[i][j];
			}
			line+="\n";
		//	System.out.println(line);
			bufRdr.write(line);
		}
		bufRdr.close();
	}

	private void writeIni(String path,String originalFile, int numArcs2, int numNodes2, int tc, int scenarios, int source2, int lastNode2) throws FileNotFoundException {
		File file = new File("./ini/Rob_"+sce+ "_"+ originalFile + ".ini");
		
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

		File file = new File(p.getParent().getParent() + "/Proyectos BSP/"+ DataFile);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;

		String[] readed = new String[4];

		int row = 0;
		int col = 0;
		
	
		
		while ((line = bufRdr.readLine()) != null && row < NumArcs + 3) {
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				readed[col] = st.nextToken();
				col++;
			}

			if (row >= 3) {
				Arcs[row - 3][0] = (Integer.parseInt(readed[0]));
				Arcs[row - 3][1] = (Integer.parseInt(readed[1]));
				Distance[row - 3] = Integer.parseInt(readed[2]);
				Time[row - 3] = Integer.parseInt(readed[3]);
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
	
	
	public static void main(String[] args) {
		try {
			rr = new Random(312);
			int cuantas = 0;
			for (int o = 30; o <=30; o++) {
				for (int i =1; i <= 9; i++) {
					for (int var = 0; var <= 2*0; var++) {
						String str_varr = null;
						if (var == 0) {
							str_varr = "a";
						} else if (var == 1) {
							str_varr = "b";
						} else {
							str_varr = "c";
						}
						str_varr ="";
						String ini = p.getParent().getParent() + "/Proyectos BSP/ini/NJ"+i+""+str_varr+".ini";
						RobGen nm = new RobGen(o+1, ini, str_varr, ("NJ"+i+str_varr));
						cuantas++;
						System.out.println("van " + cuantas);
					}
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
