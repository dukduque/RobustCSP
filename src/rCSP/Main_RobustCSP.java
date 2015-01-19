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
			for (int ins = 1; ins <=9; ins++) {
				
				ini = "ini/Rob_20_NJ"+ins+".ini";
//				ini = "ini/toy"+ins+".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				DataHandler data = new DataHandler(instance, instance.numScenarios, instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:" + instance.source + "e:" + instance.sink);
				PulseGraph network = data.getGd();
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
					spAlgo[i] = new DIKBD(network, instance.sink - 1, i);
					tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
				}
				if(numSPPs>0){
					int realSP = instance.numScenarios / numSPPs;
					for (int i = 0; i < spAlgo.length; i+=realSP) {
						DataHandler.weight_bound_availability[i]=1;
						spAlgo[i] = new DIKBD(network, instance.sink - 1, i);
						tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
					}
				}
					
				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1){
						tSp[i].start();
					}
				}

				for (int i = 0; i < spAlgo.length; i++) {
					if (DataHandler.weight_bound_availability[i] == 1)
						tSp[i].join();
				}
				
//				for (int i = 0; i < instance.NumNodes; i++) {
//					VertexPulse vv = network.vertexes[i];
//					for (int ii = 0; ii < num_weights; ii++) {
//						for (int jj = 0; jj < num_weights; jj++) {
//							if (DataHandler.weight_bound_availability[ii]== 0) {
//								vv.spMatrix[ii][jj] = 0;
//							}
//							if (vv.id == instance.source - 1) {
//								System.out.print(" " + vv.spMatrix[ii][jj]);
//							}
//						}
//						if (vv.id == instance.source - 1) {
//							System.out.println();
//						}
//					}
//				}
				System.out.println();
				double spTime =(System.currentTimeMillis() - Atime)/1000.0;
				
				
				
				for (double alpha = 0.25; alpha <=0.75; alpha+=0.25) {
					for (double beta = 0.25; beta <= 0.75; beta+=0.25) {
						for (double gamma = 0.25; gamma <= 0.75; gamma+=0.25) {
							System.out.print(ini +"/SP:/"+  spTime);
							data.calcMin();
							data.calMax();
							data.set_w(Math.round(alpha*100)/100.0);
							data.set_b(Math.round(beta*100)/100.0);
							data.setTmax(Math.round(gamma*100)/100.0);
//							DataHandler.b = 73;
//							DataHandler.w = 85;
//							DataHandler.T = 500;
//							System.out.print("/w/"+alpha+"/b/"+beta+"/T_max/"+gamma);
							ArrayList<Integer> Path = new ArrayList<Integer>();

							// /System.out.print(" /INI:/"+(System.currentTimeMillis()-Atime));
							double medicion = System.currentTimeMillis();
							int[] weights = new int[instance.numScenarios + instance.numCtrs];
							System.out.print("/bound/"+ network.getVertexByID(instance.source - 1).calcDualBound(weights,instance.source - 1));
							network.getVertexByID(instance.source - 1).pulse(0, weights, Path);
							medicion = System.currentTimeMillis() - medicion;
							System.out.print("/Pulse:/" + medicion / 1000.0);
//							System.out.print("EXE:/" + (System.currentTimeMillis() - Atime)/ 1000.0);
							System.out.println("/b:/" + PulseGraph.y_primal_bound+"/|P|/"+ PulseGraph.path.size());
							network.resetPrimalSolution();
							network.resetNetwork1();
						}
					}
				}
				
				network.vertexes = null;
				data.Arcs = null;
				data.weights = null;
				data = null;
				network = null;
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
