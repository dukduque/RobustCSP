
package RobCSP_Enum;

public class EdgePulse {
	

	
	private EdgePulse nextE;
	private int id;
	private VertexPulse source;
	private VertexPulse target;
	private int[] atributes; 
	
	public EdgePulse( VertexPulse nT, VertexPulse nH, int nid, int[] atri) {
		// TODO Auto-generated constructor stub
		
		this.source = nT;
		this.target = nH;
		this.id = nid;
		this.atributes = atri;
	}
	
	public void addNextCommonTailEdge(EdgePulse e){
		if(nextE!= null){
			nextE.addNextCommonTailEdge(e);
		}
		else{
			nextE = e;
		}
	}

	
	public EdgePulse getNext()
	{
		return nextE;
	}
	public void setNextE(EdgePulse e ){
		nextE = e;
	}
	
	public int getWeight(int obj){
		return atributes[obj];
	}
	
	public VertexPulse getSource(){
		return source;
	}
	
	public VertexPulse getTarget(){
		return target;
	}
	public int getID()
	{
		return id;
	}
	public EdgePulse findEdgebyTarget( VertexPulse targetT)
	{
		if(targetT.getID() == this.target.getID())
		{
			return this;
		}else{
			if(nextE!= null)
			{
				return nextE.findEdgebyTarget(targetT);
			}
		}
		return null;
	}
	
	
	public int getCompareCriteria(){
		int suma = 0;
		for (int i = 0; i < DataHandler.arc_weights; i++) {
			suma += target.spMatrix[i][i];
		}
		return suma;
	}

	
}
