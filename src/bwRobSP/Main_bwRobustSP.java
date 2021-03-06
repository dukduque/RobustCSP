package bwRobSP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;



public class Main_bwRobustSP {
	static Path p = Paths.get(System.getProperty("user.dir"));
	static String results_path = p.toString() + "/results/RN_results.txt";
	static String line_sep = System.getProperty("line.separator");

	public static void main(String[] args) throws InterruptedException {
		try {
			System.out.println(p.toString());
			File f = new File(results_path);
			if(f.exists()){
				f.delete();
			}
			f.createNewFile();
			int start_instance = Integer.parseInt(args[0]);
			ArrayList<SetupInfo> experiments = read_setup(start_instance);
			
			for (SetupInfo exp : experiments) {
				String ini = p.toString() + "/ini/" + exp.net_num + "_" + exp.scenarios + ".ini";
				Settings instance = new Settings(ini);

				int num_weights = instance.numScenarios;
				int numSPPs = num_weights;
				DataHandler data = new DataHandler(instance, instance.numScenarios, 0);
				data.ReadC();
				// System.out.print("Pulse_R-CSP: " + ini + "\t s:" +
				// instance.source + "e:" + instance.sink);
				PulseGraph network = data.getGd();
				long Atime = System.currentTimeMillis();
				////////
				DIKBD[] spAlgo = new DIKBD[num_weights];
				Thread[] tSp = new Thread[num_weights];
				// for (int i = 0; i < spAlgo.length; i+=realSP) {
				// DataHandler.weight_bound_availability[i]=1;
				// spAlgo[i] = new DIKBD(netWork, instance.sink - 1, i);
				// tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
				// }
				// Assure Resource constraint has a bound
				for (int i = instance.numScenarios; i < -spAlgo.length; i++) {
					numSPPs--;
					DataHandler.weight_bound_availability[i] = 1;
					spAlgo[i] = new DIKBD(network, instance.sink - 1, i);
					tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
				}
				if (numSPPs > 0) {
					int realSP = instance.numScenarios / numSPPs;
					// for (int i = 0; i < spAlgo.length; i+=realSP) {
					for (int i = 0; i < spAlgo.length; i++) {
						DataHandler.weight_bound_availability[i] = 1;
						spAlgo[i] = new DIKBD(network, instance.sink - 1, i);
						tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
					}
				}

				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1) {
						tSp[i].start();
					}
				}

				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1){
						tSp[i].join();
					}
				}
				double spTime = (System.currentTimeMillis() - Atime) / 1000.0;
				for (double num_labels = 0.5; num_labels <= 1.5; num_labels += 0.5) {
					for (int reps = 0; reps < 10; reps++) {
						String  exp_out_info = exp.net_num + "/" + instance.numScenarios + "/" + spTime;
						DataHandler.numLabels = (int) (num_labels * DataHandler.scenarios);
						DataHandler.w = exp.w;
						DataHandler.b = exp.b;
						
						exp_out_info += "/" + num_labels + "/" + reps  + "/" + exp.w + "/" + exp.b;
						// W:94214 0.5 65642
						ArrayList<Integer> Path = new ArrayList<Integer>();
						ArrayList<Integer> path_arcs = new ArrayList<Integer>();
						double medicion = System.nanoTime();//currentTimeMillis();
						int[] weights = new int[instance.numScenarios];
						network.getVertexByID(instance.source - 1).pulse(0, weights, Path, path_arcs);
						medicion = (System.nanoTime() - medicion) /1000000000.0;
						String pulse_time = String.format("%6f" , medicion);
						String total_time = String.format("%6f" , (spTime+medicion));
						exp_out_info += "/"
								+ network.getVertexByID(instance.source - 1).calcDualBound(weights,
										instance.source - 1)
								+ "/" + pulse_time + "/" + total_time + "/" + PulseGraph.y_primal_bound
								+ "/" + (PulseGraph.y_primal_bound == exp.of)+line_sep;
						System.out.print(exp_out_info);
						
						// network.a_Posteriori_evaluation();
						// System.out.println(network.path);
						
						network.resetPrimalSolution();
						network.resetNetwork1();
						appendStrToFile(results_path, exp_out_info);
					}
				}
				network.vertexes = null;
				data.Arcs = null;
				data.weights = null;
				data = null;
				network = null;
				//System.gc();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads a csv file with the experiment setup: Network name Network id
	 * Values for w and b Objective function for validation
	 */
	public static ArrayList<SetupInfo> read_setup(int start_num) {
		ArrayList<SetupInfo> experiments = new ArrayList<SetupInfo>();

		File file = new File(p.toString() + "/data/RN_setup/bwRobSP_setup.csv");
		BufferedReader bufRdr;
		try {
			bufRdr = new BufferedReader(new FileReader(file));

			String line = bufRdr.readLine(); // Read headers

			String[] read = new String[9];

			int col = 0;
			while ((line = bufRdr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");
				while (st.hasMoreTokens()) {
					// get next token and store it in the array
					read[col] = st.nextToken();
					col++;
				}
				col = 0;
				SetupInfo instance = new SetupInfo();
				instance.net = read[1];
				instance.net_num = read[2];
				
				int num = Integer.parseInt(instance.net_num.substring(2));
				instance.scenarios = Integer.parseInt(read[3]);
				if (instance.net.equals("RI") && instance.scenarios == 1000  && num==start_num) {
					instance.w = Integer.parseInt(read[4]);
					instance.b = Integer.parseInt(read[5]);
					instance.of = Integer.parseInt(read[8]);
					experiments.add(instance);
				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return experiments;

	}
	
	
	public static void appendStrToFile(String fileName, String str) {
		try {

			// Open given file in append mode.
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			out.write(str);
			out.close();
		} catch (IOException e) {
			System.out.println("exception occoured" + e);
		}
	}
}
