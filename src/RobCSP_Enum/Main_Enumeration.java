package RobCSP_Enum;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;



public class Main_Enumeration {
	static Path p = Paths.get(System.getProperty("user.dir"));
	

	public static void main(String[] args) throws InterruptedException {
		
		try {
			int[] RCSPnetworks = { 5,  7,   13,  15,  21, 23 };
			String[] names = {"DC"};
//			String[] names = {"NJ"};
			int num_weights =0;
			String ini;
			
			for (int name = 0; name < names.length; name++) {
				String net_name = names[name];
				for (int sce = 100; sce <= 1000; sce += 1000000) {
					for (int ins = 1; ins <= 1; ins++) {
						ini = "ini/ToyExamples/" + "sm9.ini";
						Settings instance = new Settings(ini);
						num_weights = instance.numScenarios + instance.numCtrs;
						int numSPPs = num_weights;
						DataHandler data = new DataHandler(instance, instance.numScenarios, instance.numCtrs);
						data.ReadC();
						PulseGraph network = data.getGd();
						long Atime = System.currentTimeMillis();
						DIKBD[] spAlgo = new DIKBD[num_weights];
						Thread[] tSp = new Thread[num_weights];
						// Assure Resource constraint has a bound
						for (int i = instance.numScenarios; i < spAlgo.length; i++) {
							numSPPs--;
							DataHandler.weight_bound_availability[i]=1;
							spAlgo[i] = new DIKBD(network, instance.sink - 1, i);
							tSp[i] = new Thread(new ShortestPathTask(i, spAlgo[i]));
						}
						if(numSPPs>0){
							int realSP = instance.numScenarios / numSPPs;
	//						for (int i = 0; i < spAlgo.length; i+=realSP) {
							for (int i = 0; i < spAlgo.length; i++) {
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
						System.out.println("done");
					double spTime =(System.currentTimeMillis() - Atime)/1000.0;
					for (double alpha = 1; alpha <=1.0; alpha+=0.25) {
						for (double beta = 0.1; beta <= 1.0; beta+=0.1) {
							for (double gamma = 1; gamma <= 1.0; gamma+=0.25) {
								System.out.print(names[name]+ins+"/"+instance.numScenarios+"/"+  spTime);
	//							System.out.print(ini + "\t");
								data.calcMin();
								data.calMax();
								data.set_w(Math.round(alpha*100)/100.0);
								data.set_b(Math.round(beta*100)/100.0);
								data.setTmax(Math.round(gamma*100)/100.0);
	
								ArrayList<Integer> Path = new ArrayList<Integer>();
								ArrayList<Integer> path_arcs = new ArrayList<Integer>();
								double medicion = System.currentTimeMillis();
								int[] weights = new int[instance.numScenarios + instance.numCtrs];
								network.getVertexByID(instance.source - 1).pulse(0, weights, Path, path_arcs);
								medicion = (System.currentTimeMillis() - medicion)/ 1000.0;
								System.out.print("/"+ network.getVertexByID(instance.source - 1).calcDualBound(weights,instance.source - 1));
								System.out.print("/" + medicion );
								System.out.print("/" + (spTime+medicion));
								System.out.print("/" + PulseGraph.y_primal_bound);//+"/|P|/"+ PulseGraph.path.size());
								System.out.print("/");
								System.out.print(PulseGraph.totalPaths);
								System.out.print("/");
								System.out.println(PulseGraph.totalFeasiblePaths);
								System.out.println(network.effSet.size());
								System.out.println(network.effSet);
//								System.out.println(DataHandler.T);
//								network.a_Posteriori_evaluation();
	//							System.out.println();
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

				}
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
