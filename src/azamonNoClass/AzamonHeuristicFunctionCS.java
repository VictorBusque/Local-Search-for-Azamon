package azamonNoClass;

import java.util.ArrayList;

import aima.search.framework.HeuristicFunction;

public class AzamonHeuristicFunctionCS implements HeuristicFunction{

	public double getHeuristicValue(Object n) {
		AzamonState state=(AzamonState)n;
		ArrayList<Double> h = state.heuristicCalc();
		double c = h.get(0); //c es negativa
		double s = h.get(1);
		double mC = state.getState().length*5;
		double mS = state.getState().length*3;
		return 	1/(c/mC+s/mS);
	}
}