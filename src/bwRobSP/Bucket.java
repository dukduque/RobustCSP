package bwRobSP;

import sun.security.provider.certpath.Vertex;



public class Bucket {
	
	private VertexPulse entrance;
	private int key;
	private int obj;
	/**
	 * Create an instance of a bucket. If a bucket
	 * is opened, a new vertex is being added
	 * @param v
	 */
	public Bucket(VertexPulse v, int nKey, int obje){
		entrance = v;
		key = nKey;
		obj = obje;
	}
	public Bucket(int nKey, int obje){
		key = nKey;
		obj = obje;
	}
	public Bucket(VertexPulse v, int nKey){
		entrance = v;
		key = nKey;
	}
	
	public Bucket(int nKey){
		key = nKey;
	}
	
	
	/**
	 * Insert a vertex in the bucket.
	 * @param v Vertex being inserted
	 */
	public void insertVertex(VertexPulse v){
		if(entrance!=null){
			//entrance.insertVertex(obj, v);
			
			v.left[obj] = entrance.left[obj];
			v.rigth[obj]  = entrance;
			entrance.left[obj].rigth[obj] = v;
			entrance.left[obj] = v;
		}else{
			entrance = v;
		}
	}
	
	/**
	 * This methods remove from the bucket a node which is 
	 * been labeling. This method also unlink the node from every body else
	 * @return true if the bucket gets empty, false otherwise
	 */
	public boolean deleteLabeledVertex(){
		//Delete entrance / FIFO policy 
		entrance = entrance.rigth[obj];
		boolean emp = false;
		VertexPulse v = entrance.left[obj];
		
		//entrance.left[obj].unLinkVertex(obj);
		if (v.rigth[obj].getID() == v.id) {
			v.left[obj] = v;
			v.rigth[obj] = v;
			emp =  true;
		} else {
			v.left[obj].rigth[obj] =v.rigth[obj];
			v.rigth[obj].left[obj] = v.left[obj];
			v.left[obj] = v;
			v.rigth[obj] = v;
			emp =   false;
		}
		
		
		if(emp){
			entrance = null;
			return true;
		}
		return false;
	}
	
	
	
	public boolean deleteToMove(VertexPulse v){
		if(entrance.getID() == v.getID()){
			entrance = entrance.rigth[obj];
		}
		boolean emp;
		if (v.rigth[obj].getID() == v.id) {
			v.left[obj] = v;
			v.rigth[obj] = v;
			emp =  true;
		} else {
			v.left[obj].rigth[obj] =v.rigth[obj];
			v.rigth[obj].left[obj] = v.left[obj];
			v.left[obj] = v;
			v.rigth[obj] = v;
			emp =  false;
		}
		
		if(emp){
			entrance = null;
			return true;
		}
		return false;
	}
	
	
	public int getKey(){
		return key;
	}
	public void setKey(int nKey){
		key = nKey;
	}
	
	public VertexPulse getEntrance(){
		return  entrance;
	}
	
	public boolean empty() {
		if(entrance!=null){
			return false;
		}
		return true;
	}
}
