package rCSP_IP;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;



public class Main_IP {
	static Path p = Paths.get(System.getProperty("user.dir"));
	

	public static void main(String[] args) throws InterruptedException {
//		runToy();
		runRCSP();
//		runNJ();
	}
	
	public static void runRCSP() {
		try {

			int num_weights = 0;
			String ini;
			for (int ins = 1; ins <= 1; ins++) {

				ini = "ini/RCSP/rcsp5_3_1.ini";
			
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios,
						instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:"
						+ instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void runToy(){
		
		try {
			
			int num_weights =0;
			String ini;
			for (int ins = 1; ins <=1; ins++) {
				
//				ini = "ini/Rob_20_NJ"+ins+".ini";
				ini = "ini/toy"+ins+".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios, instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:" + instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public static void runNJ(){
		
		try {
			
			int num_weights =0;
			String ini;
			for (int ins = 1; ins <=1; ins++) {
				
				ini = "ini/Rob_20_NJ"+ins+".ini";
				//ini = "ini/toy"+ins+".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios, instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:" + instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
