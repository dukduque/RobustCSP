package RobCSP_IP;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class OptModel {
	
	
	GRBEnv env;
	GRBModel model;
	GRBVar[] x_ij;
	GRBVar[] y;
	IP_DH data;
	int w;
	int b;
	int Tmax;
	public OptModel(IP_DH nData, int nW, int nB, int nT) {
		data = nData;
		w=nW;
		b=nB;
		Tmax=nT;
		try {
			initilizeGurobi();
			createVariables();
			createConstraints();
			optimize();
			printSol();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	
	private void optimize() throws GRBException {
		long tnow= System.currentTimeMillis();
		double medicion =0;
		model.optimize();
		medicion = (System.currentTimeMillis()-tnow)/1000.0;
		System.out.print("\t EXE:"+medicion);
		
	}


	public void initilizeGurobi() throws GRBException{
		env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
		env.set(GRB.DoubleParam.TimeLimit, 3600);
		model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
	}
	
	public void createVariables() throws GRBException{
		x_ij = new GRBVar[data.NumArcs];
		for (int i = 0; i < x_ij.length; i++) {
			x_ij[i] = model.addVar(0, 1, 0, GRB.BINARY, ("x_"+data.Arcs[i][0]+","+data.Arcs[i][1]));
		}
		y = new GRBVar[data.scenarios];
		for (int k = 0; k < y.length; k++) {
			y[k] = model.addVar(0, 1, 1, GRB.BINARY, "y_"+k);
		}
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
		
		//Resource ctrrr
		GRBLinExpr res_ctr = new GRBLinExpr();
		for (int i = 0; i < x_ij.length; i++) {
			res_ctr.addTerm(data.weights[i][data.scenarios], x_ij[i]);
		}
		model.addConstr(res_ctr, GRB.LESS_EQUAL, Tmax, "res_ctr");

		//Scenarios and y_k ctrs
		for (int k = 0; k < data.scenarios; k++) {
			GRBLinExpr left_exp = new GRBLinExpr();
			GRBLinExpr right_exp = new GRBLinExpr();
			for (int i = 0; i < x_ij.length; i++) {
				left_exp.addTerm(data.weights[i][k],x_ij[i]);
			}
			right_exp.addTerm(b, y[k]);
			right_exp.addTerm(-w, y[k]);
			right_exp.addConstant(w);
			model.addConstr(left_exp, GRB.LESS_EQUAL, right_exp, "s:"+k);
		}
		model.update();
		
	}
	public void printSol() throws GRBException{
		int status = model.get(GRB.IntAttr.Status);
		if(status==GRB.OPTIMAL){
			System.out.println("|b|" + model.get(DoubleAttr.ObjVal));
		}else{
			System.out.println("|b|" + -1);
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
