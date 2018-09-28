package MinMax_RobCSP_IP;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class MinMaxOptModel {
	
	
	GRBEnv env;
	GRBModel model;
	GRBVar[] x_ij;
	GRBVar z;
	IP_DH data;
	int w;
	int b;
	int Tmax;
	public MinMaxOptModel(IP_DH nData, int nTmax, int cpuTime) {
		data = nData;
		Tmax = nTmax;
		try {
			initilizeGurobi(cpuTime);
			createVariables();
			createConstraints();
			optimize();
			printSol();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	
	private void optimize() throws GRBException {
		model.write("GRB.lp");
		long tnow= System.currentTimeMillis();
		double medicion =0;
		model.optimize();
		medicion = (System.currentTimeMillis()-tnow)/1000.0;
		System.out.print("\t EXE:"+medicion);
		
	}


	public void initilizeGurobi(int cpuTime) throws GRBException{
		env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
		env.set(GRB.DoubleParam.TimeLimit, cpuTime);
		model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
		
	}
	
	public void createVariables() throws GRBException{
		x_ij = new GRBVar[data.NumArcs];
		for (int i = 0; i < x_ij.length; i++) {
			x_ij[i] = model.addVar(0, 1, 0, GRB.BINARY, ("x_"+data.Arcs[i][0]+","+data.Arcs[i][1]));
		}
		z = model.addVar(0, Double.POSITIVE_INFINITY, 1, GRB.CONTINUOUS, "Z_cachitos");
		model.update();
	}
	
	public void createConstraints() throws GRBException{
		//Flow balance ctrs
		for (int i = 0; i < data.NumNodes; i++) {
			GRBLinExpr flow_ctr = new GRBLinExpr();
			//out flow
			for (int m_index = 0; m_index < data.fwd_arcs[i].size(); m_index++) {
				int arc_index = data.fwd_arcs[i].get(m_index);
				flow_ctr.addTerm(1,x_ij[arc_index]);
			}
			//in flow
			for (int m_index = 0; m_index < data.bwd_arcs[i].size(); m_index++) {
				int arc_index = data.bwd_arcs[i].get(m_index);
				flow_ctr.addTerm(-1,x_ij[arc_index]);
			}
			int b_i = i==data.Source?1:i==data.LastNode?-1:0;
			model.addConstr(flow_ctr, GRB.EQUAL, b_i, "Flow"+i);
		}
		
		//Resource ctr
		GRBLinExpr res_ctr = new GRBLinExpr();
		for (int i = 0; i < x_ij.length; i++) {
			res_ctr.addTerm(data.weights[i][data.scenarios], x_ij[i]);
		}
		model.addConstr(res_ctr, GRB.LESS_EQUAL, Tmax, "res_ctr");

		//Min Max ctrs
		for (int k = 0; k < data.scenarios; k++) {
			GRBLinExpr k_cost_exp = new GRBLinExpr();
			for (int i = 0; i < x_ij.length; i++) {
				k_cost_exp.addTerm(data.weights[i][k],x_ij[i]);
			}
			model.addConstr(z, GRB.GREATER_EQUAL, k_cost_exp, "z:"+k);
		}
		model.update();
		
	}
	public void printSol() throws GRBException{
		int status = model.get(GRB.IntAttr.Status);
		if(status==GRB.OPTIMAL){
			System.out.println("\tZ:" + model.get(DoubleAttr.ObjVal)+ "  " + z.get(GRB.DoubleAttr.X));
		}else{
			System.out.println("\tZ:"  + -1 + " status: " + status);
		}
		
//		for (int i = 0; i < x_ij.length; i++) {
//			if (x_ij[i].get(GRB.DoubleAttr.X)>0) {
//				System.out.println("arc_"+" " +x_ij[i].get( GRB.StringAttr.VarName));
//			}
//		}
	}


	public void shutdown() throws GRBException {
		model.dispose();
		env.dispose();
		
	}

}
