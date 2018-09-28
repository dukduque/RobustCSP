package MinMax_RobCSP_IP;

import gurobi.GRBException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main_MinMaxIP {
	static Path p = Paths.get(System.getProperty("user.dir"));

	public static void main(String[] args) throws InterruptedException, NumberFormatException, IOException {
		// runToy();
		// runRCSP();
		// runRN();
		try {
			Main_MinMaxIP ip_solver = new Main_MinMaxIP();
			ip_solver.runSettings();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public void runSettings() throws NumberFormatException, IOException, GRBException {
		ArrayList<MinMaxIP_Setting> instances = load("ALL", 10);
		String ini;
		for (int i = 0; i < instances.size();i++) {
			MinMaxIP_Setting config = instances.get(i);
			System.out.print("MinMax_IP//"+ config.toString());
			ini = config.iniFile;
			Settings instance = new Settings(ini);
			IP_DH data = new IP_DH(instance, instance.numScenarios,	instance.numCtrs);
			data.ReadC();
//			System.out.print("Pulse_R-CSP: " + ini + "\t s:"+ instance.source + "e:" + instance.sink);
			MinMaxOptModel IP = new MinMaxOptModel(data,config.T, 3600);
			IP.shutdown();
		}
		
	}



	public  ArrayList<MinMaxIP_Setting> load(String string, int i) throws NumberFormatException, IOException {
		ArrayList<MinMaxIP_Setting> retorno = new ArrayList<Main_MinMaxIP.MinMaxIP_Setting>();
		File file = new File("Settings/MinMax_IP/"+string+"_"+i+"_settings.txt");
		BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
		String line = null;
		String readed[];
		while((line = bufRdr.readLine()) != null)
		{	
			readed = line.split("\t");
			MinMaxIP_Setting config = new MinMaxIP_Setting();
			config.iniFile = readed[0];
			config.T = Integer.parseInt(readed[1]);
			retorno.add(config);
		}
		
		
		
		return retorno;
	}

	private class  MinMaxIP_Setting{
		String iniFile;
		int T;

		@Override
		public String toString() {
			return iniFile + "\t" + T;
		}
	}
}
