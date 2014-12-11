package rCSP;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;



public class Main_RobustCSP {
	static Path p = Paths.get(System.getProperty("user.dir"));
	

	public static void main(String[] args) throws InterruptedException {
		
		try {
			
			int num_weights =0;
			String ini;
			for (int ins = 1; ins <= 9; ins++) {
				int numSPPs = 11;
				ini = "ini/Rob_10_NJ"+ins+".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				System.out.println(num_weights);
				System.out.print("Pulse_MSP: " + ini + "\t s:" + instance.source + "e:" + instance.sink);
				DataHandler data = new DataHandler(instance, instance.numScenarios, instance.numCtrs);
				data.ReadC();
				PulseGraph netWork = data.getGd();
				long Atime = System.currentTimeMillis();
				DIKBD[] spAlgo = new DIKBD[num_weights];
				Thread[] tSp = new Thread[num_weights];
//				for (int i = 0; i < spAlgo.length; i+=realSP) {
//					DataHandler.weight_bound_availability[i]=1;
//					spAlgo[i] = new DIKBD(netWork, instance.sink - 1, i);
//					tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
//				}
				//Assure Resource constraint has a bound
				for (int i = instance.numScenarios; i < spAlgo.length; i++) {
					numSPPs--;
					DataHandler.weight_bound_availability[i]=1;
					spAlgo[i] = new DIKBD(netWork, instance.sink - 1, i);
					tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
				}
				if(numSPPs>0){
					int realSP = instance.numScenarios / numSPPs;
					for (int i = 0; i < spAlgo.length; i+=realSP) {
						DataHandler.weight_bound_availability[i]=1;
						spAlgo[i] = new DIKBD(netWork, instance.sink - 1, i);
						tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
					}
				}
					
				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1)
						tSp[i].start();
				}

				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1)
						tSp[i].join();
				}
				 System.out.println();

				for (int i = 0; i < instance.NumNodes; i++) {
					VertexPulse vv = netWork.vertexes[i];
					for (int ii = 0; ii < num_weights; ii++) {
						for (int jj = 0; jj < num_weights; jj++) {
							if (DataHandler.weight_bound_availability[ii]== 0) {
								vv.spMatrix[ii][jj] = 0;
							}
							if (vv.id == instance.source - 1) {
								System.out.print(" " + vv.spMatrix[ii][jj]);
							}
						}
						if (vv.id == instance.source - 1) {
							System.out.println();
						}
					}
				}
				System.out.print(" /SP:/"+ (System.currentTimeMillis() - Atime));
				// System.out.println();
				// for (int i = 0; i < obj; i++) {
				// for (int j = 0; j < obj; j++) {
				// System.out.print(netWork.getVertexByID(Instance.Source-1).spMatrix[i][j]+
				// " , ");
				// }System.out.println();
				// }
				//

				ArrayList<Integer> Path = new ArrayList<Integer>();

				// /System.out.print(" /INI:/"+(System.currentTimeMillis()-Atime));
				double medicion = System.currentTimeMillis();
				int[] objs = new int[instance.numScenarios + instance.numCtrs];
				netWork.getVertexByID(instance.source - 1).pulse(objs, Path);

				medicion = System.currentTimeMillis() - medicion;

				// netWork.printEF();

				System.out.print("/Pulse:/" + medicion / 1000.0);
				System.out.print("EXE:/" + (System.currentTimeMillis() - Atime)
						/ 1000.0);
				System.out.println("/b:/" + netWork.y_primal_bound);
				// for (int j = 0; j < netWork.PathObj.length; j++) {
				// System.out.print(objectives[cuales[j]]+ " ");
				// }System.out.println();
				// for (int i = 0; i < netWork.PathObj[0].size() ; i++) {
				//
				// for (int j = 0; j < netWork.PathObj.length; j++) {
				// System.out.print(netWork.PathObj[j].get(i) + " //");
				// }
				// System.out.print(netWork.Paths.get(i));
				// System.out.println();
				// }
				//
				netWork.vertexes = null;
				data.Arcs = null;
				data.weights = null;
				data = null;
				netWork = null;

			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
