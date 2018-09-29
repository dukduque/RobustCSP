package bwRobSP;

/**
 * 2018/09/27
 * @author dduque
 *	This class was added later to read a setup file based
 *	on preliminary results that contained the values for the
 *	experiment setup described in the paper NETWORKS.
 */
public class SetupInfo{
	String net;
	String net_num;
	int scenarios;
	int w;
	int b;
	int of;
	public SetupInfo(){
		
	}
	
	public boolean validateResult(int result){
		return result == of;
	}
	
	
}