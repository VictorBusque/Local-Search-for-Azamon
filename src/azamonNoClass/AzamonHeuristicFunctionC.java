package azamonNoClass;

import java.util.ArrayList;

import aima.search.framework.HeuristicFunction;


public class AzamonHeuristicFunctionC implements HeuristicFunction{

	public double getHeuristicValue(Object n) {
		AzamonState state=(AzamonState)n;
		ArrayList<Double> h = state.heuristicCalc();
		double c = h.get(0); // c es negativa
		return 1/c;
	}
}