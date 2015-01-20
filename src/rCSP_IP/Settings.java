package rCSP_IP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import sun.launcher.resources.launcher;

public class Settings {

	String DataFile;
	int NumArcs;
	int NumNodes;
	int sink;
	int source;
	int numScenarios;
	int numCtrs;
	int seed;
	int Tmax;
	
	ArrayList<Integer> sinks = new ArrayList<Integer>();
	ArrayList<Integer> sources=new ArrayList<Integer>();


	
	public Settings(String ConfigFile) throws IOException{
		
		File file = new File(ConfigFile);
		 
		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
		String line = null;
		
		String [][] readed = new String [7][2];
		
		int row = 0;
		int col = 0;
	 
		//read each line of text file
		while((line = bufRdr.readLine()) != null && row < 7)
		{	
		StringTokenizer st = new StringTokenizer(line,":");
		while (st.hasMoreTokens())
		{
			//get next token and store it in the array
			readed[row][col] = st.nextToken();
			col++;
			
		}
		col = 0;
		row++;
		
		}
		Path p = Paths.get(System.getProperty("user.dir"));	
		DataFile= readed[0][1];
		NumArcs=Integer.parseInt(readed[1][1]);
		NumNodes=Integer.parseInt(readed[2][1]);
		Tmax=Integer.parseInt(readed[3][1]);
		numScenarios=Integer.parseInt(readed[4][1]);
		
		source=Integer.parseInt(readed[5][1]);		 
		sink=Integer.parseInt(readed[6][1]);
		
		numCtrs = 1;
		
	}

	public int getNumberOfInstances() {
		if (source==-1 && sink==-1) {
			int top  =  10 ; 
			Random r1 = new Random(0);
			int nodes  = NumNodes;
			for (int i = 0; i <top; i++) {
				sources.add(1+ r1.nextInt(nodes)); 
				sinks.add(1+ r1.nextInt(nodes)); 
			}System.out.println();
			System.out.println("sources:"+ sources);
			System.out.println("sinks:" + sinks);
			
			return top;
		}else{
			return 1;
		}
	}
	
	
}
