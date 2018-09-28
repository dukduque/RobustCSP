package RobCSP;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;



public class Main_RobustCSP {
	static Path p = Paths.get(System.getProperty("user.dir"));
	

	public static void main(String[] args) throws InterruptedException {
		try {
			System.out.println(p.toString());
			int[] RCSPnetworks = {2};//{ 5,  7,   13,  15,  21, 23 };
			int[] numScenarios = {500};//{10, 50, 100, 200, 500, 1000,10000}; 
			//String[] names = {"rcsp"};
			String[] names = {"RI"};
			int num_weights =0;
			String ini;
			
			for (int name = 0; name < names.length; name++) {
				String net_name = names[name];
				for (int sce = 0; sce < numScenarios.length; sce++) {
					for (int ins = 0; ins < RCSPnetworks.length; ins++) {

						ini = p.toString()+"/ini/"+net_name+RCSPnetworks[ins]+"_"+numScenarios[sce]+".ini";
//						ini = "ini/ToyExamples/sm9"+"_"+sce+".ini";
						Settings instance = new Settings(ini);
						num_weights = instance.numScenarios + instance.numCtrs;
						int numSPPs = num_weights;
					DataHandler data = new DataHandler(instance, instance.numScenarios, instance.numCtrs);
					data.ReadC();
	//				System.out.print("Pulse_R-CSP: " + ini + "\t s:" + instance.source + "e:" + instance.sink);
					PulseGraph network = data.getGd();
					long Atime = System.currentTimeMillis();
					////////
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
		

					
					double spTime =(System.currentTimeMillis() - Atime)/1000.0;
					
					for (double alpha = 0.25; alpha <=0.75; alpha+=0.25) {
						for (double beta = 0.25; beta <= 0.75; beta+=0.25) {
							for (double gamma = 10; gamma <= 10; gamma+=10) {
								System.out.print(names[name]+RCSPnetworks[ins]+"/"+instance.numScenarios+"/"+  spTime);
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
								network.a_Posteriori_evaluation();
//								System.out.println(network.path);
								System.out.println();
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
