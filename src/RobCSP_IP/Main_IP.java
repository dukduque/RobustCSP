package RobCSP_IP;

import gurobi.GRBException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main_IP {
	static Path p = Paths.get(System.getProperty("user.dir"));

	public static void main(String[] args) throws InterruptedException, NumberFormatException, IOException {
		// runToy();
		// runRCSP();
		// runRN();
		try {
			Main_IP ip_solver = new Main_IP();
			ip_solver.runSettings();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runSettings() throws NumberFormatException, IOException, GRBException {
		ArrayList<IP_Setting> instances = load("NJ", 10);
		int num_weights = 0;
		String ini;
		for (int i = 113; i < instances.size();i++) {
			IP_Setting config = instances.get(i);
			System.out.print("IP_bw-rob//"+ config.toString());
			ini = config.iniFile;
			Settings instance = new Settings(ini);
			num_weights = instance.numScenarios + instance.numCtrs;
			IP_DH data = new IP_DH(instance, instance.numScenarios,	instance.numCtrs);
			data.ReadC();
//			System.out.print("Pulse_R-CSP: " + ini + "\t s:"+ instance.source + "e:" + instance.sink);
			OptModel IP = new OptModel(data,config.w,config.b,config.T);
			IP.shutdown();
		}
		
		
		
	}

	
	public static void runRCSP() {
		try {
			int[] networks = { 5, 7, 13, 15, 21, 23 };
			int num_weights = 0;
			String ini;
			for (int ins = 1; ins < networks.length; ins++) {

				ini = "ini/RCSP/rcsp" + networks[ins] + "_3.ini";

				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios,
						instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:"
						+ instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
				IP.shutdown();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void runToy() {

		try {

			int num_weights = 0;
			String ini;
			for (int ins = 1; ins <= 1; ins++) {

				// ini = "ini/Rob_20_NJ"+ins+".ini";
				ini = "ini/toy" + ins + ".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios,
						instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:"
						+ instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
				IP.shutdown();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void runRN() {

		try {
			String RN_kind = "DC";
			int num_weights = 0;
			String ini;
			for (int ins = 1; ins <= 1; ins++) {

				ini = "ini/RoadNetworks/Rob_10_" + RN_kind + ins + ".ini";
				// ini = "ini/toy"+ins+".ini";
				Settings instance = new Settings(ini);
				num_weights = instance.numScenarios + instance.numCtrs;
				int numSPPs = num_weights;
				IP_DH data = new IP_DH(instance, instance.numScenarios,
						instance.numCtrs);
				data.ReadC();
				System.out.print("Pulse_R-CSP: " + ini + "\t s:"
						+ instance.source + "e:" + instance.sink);
				OptModel IP = new OptModel(data);
				IP.shutdown();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  ArrayList<IP_Setting> load(String string, int i) throws NumberFormatException, IOException {
		ArrayList<IP_Setting> retorno = new ArrayList<Main_IP.IP_Setting>();
		File file = new File("Settings/RobCSP_IP/"+string+"_"+i+"_settings.txt");
		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
		String line = null;
		String [] readed = new String [4];
		while((line = bufRdr.readLine()) != null)
		{	
			readed = line.split("\t");
			IP_Setting config = new IP_Setting();
			config.iniFile = readed[0];
			config.w = Integer.parseInt(readed[1]);
			config.b = Integer.parseInt(readed[2]);
			config.T = Integer.parseInt(readed[3]);
			retorno.add(config);
		}
		
		
		
		return retorno;
	}

	private class  IP_Setting{
		String iniFile;
		int w;
		int b;
		int T;
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return iniFile+"\t"+w+"\t"+b+"\t"+T;
		}
	}
}
