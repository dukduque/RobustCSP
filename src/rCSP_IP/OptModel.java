package rCSP_IP;

import java.util.ArrayList;

import rCSP.DataHandler;
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
	int w=85;
	int b=73;
	int Tmax=500;
	public OptModel(IP_DH nData) {
		data = nData;
		try {
			initilizeGurobi();
			createVariables();
			createConstraints();
			model.write("data/Toys/gurobi.lp");
			model.optimize();
			printSol();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	
	public void initilizeGurobi() throws GRBException{
		env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
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
		model.addConstr(res_ctr, GRB.LESS_EQUAL, 500, "res_ctr");

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
		System.out.println("Ybar=" + model.get(DoubleAttr.ObjVal));
		for (int i = 0; i < x_ij.length; i++) {
			if (x_ij[i].get(GRB.DoubleAttr.X)>0) {
				System.out.println(x_ij[i].get(GRB.StringAttr.VarName));
			}
		}
	}

}
