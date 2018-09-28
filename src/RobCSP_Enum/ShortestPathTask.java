package RobCSP_Enum;

public class ShortestPathTask implements Runnable {

	private DIKBD dikbd;
	private int objctive;
	private int algoRuning;
	
	
	public ShortestPathTask(int obj, DIKBD sp) {
		//quienES 1 Dist, 0 Time;
		objctive = obj;
		dikbd = sp;
	}
	
	@Override
	public void run() {
		dikbd.runAlgorithm();
	}	
	
}
