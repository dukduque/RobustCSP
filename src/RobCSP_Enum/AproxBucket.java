package RobCSP_Enum;



public class AproxBucket {
	
	private AproxBucket down;
	private AproxBucket up;
	private VertexPulse entrance;
	
	/**
	 * Key of the bucket, also is the lower bound
	 * of the bucket
	 */
	private int key;
	/**
	 * upper is the upper bound of the bucket
	 */
	private int upper;
	
	/**
	 * lower is the upper bound of the bucket
	 */
	private int lower;
	
	/**
	 * objective which is beeing optmized
	 */
	private int obj;
	/**
	 * Create an instance of an aproximate bucket. If a bucket
	 * is opened, a new vertex is being added
	 * @param v
	 */
	public AproxBucket(VertexPulse v, int nKey, int obje, int delta){
		down = null;
		up = null;
		entrance = v;
		key = nKey;
		lower = key*delta;
		upper = (key+1)*delta-1;
		obj = obje;
	}
	
	/**
	 * Insert a vertex in the bucket.
	 * @param v Vertex being inserted
	 */
	public void insertVertex(VertexPulse v){
		//entrance.insertVertex(obj , v);
		
		v.left[obj] = entrance.left[obj];
		v.rigth[obj]  = entrance;
		entrance.left[obj].rigth[obj] = v;
		entrance.left[obj] = v;
	}

	/**
	 * 
	 * @return
	 */
	public AproxBucket deleteLabeledBucket() {
		if(up!=null){
			up.down = null;
			return up;
		}
		return null;
	}
	 
	public void deleteToPass(VertexPulse v){
		entrance = entrance.rigth[obj];
		v.left[obj] = v;
		v.rigth[obj] = v;
	}
	
	
	public boolean deleteToMove(VertexPulse v){
		if(entrance.getID() == v.getID()){
			entrance = entrance.rigth[obj];
		}
		
		if (v.rigth[obj].getID() == v.id) {
			v.left[obj] = v;
			v.rigth[obj] = v;
			return true;
		} else {
			v.left[obj].rigth[obj] =v.rigth[obj];
			v.rigth[obj].left[obj] = v.left[obj];
			v.left[obj] = v;
			v.rigth[obj] = v;
			return  false;
		}
		
		
		//return v.unLinkVertex(obj);
	}
	
	

	
	public AproxBucket deleteBucketToEmpty(){
		if(up!=null){
			up.down = null;
			return up;
		}
		return null;
	}
	
	public AproxBucket deleteBucket(){
		if(up!=null){
			up.down = down;
			if(down != null){
				down.up = up;
			}else{
				return null;
			}
		}
		else{
			if(down != null){
				down.up = null;
				return down;
			}else{
				return null;
			}
		}
		return null;
	}
	
	public int getKey(){
		return key;
	}
	
	public int getLowerBound()
	{
		return lower;
	}
	
	public int getUpperBound()
	{
		return upper;//No me gusta llamar a delta así
	}
	
	public VertexPulse getEntrance(){
		return  entrance;
	}
	public AproxBucket getUP(){
		return up;
	}
	public AproxBucket getDown(){
		return down;
	}
	public void setUP(AproxBucket v){
		up = v;
	}
	public void setDown(AproxBucket v){
		down = v;
	}
	public void turnTheBucket(){
		entrance= entrance.rigth[obj];
	}
	
}
