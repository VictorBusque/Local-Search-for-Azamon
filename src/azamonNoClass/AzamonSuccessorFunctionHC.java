package azamonNoClass;

import java.util.ArrayList;
import java.util.List;

import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class AzamonSuccessorFunctionHC implements SuccessorFunction {

	private Paquetes p;
	private Transporte t;
	
	public AzamonSuccessorFunctionHC(Paquetes p, Transporte t) {
		this.p = p;
		this.t = t;
	}
	
	public List getSuccessors(Object aState) {
		ArrayList retVal = new ArrayList();
		AzamonState state = (AzamonState) aState;
		for (int i = 0; i < p.size(); i++) {
			for (int j = i+1; j < p.size(); j++) { //for each possible swap
				if (state.checkSwap(i, j)) {
					AzamonState newState = new AzamonState(state.getState());
					newState.swapPckts(i, j);
					retVal.add(new Successor("Swapped packet: " + i + " for " + j+"  Offer " + state.getOferta(i) + " <-> " + state.getOferta(j),newState));
				}
			}
			for (int k = 0; k < t.size(); k++) {//for each possible offer.
				if (state.getState()[i] != k && state.checkSelectOffer(i, k)) {
					AzamonState newState1 = new AzamonState(state.getState());
					newState1.selectOffer(i, k);
					retVal.add(new Successor("Selected offer: " + k + " for packet " +i,newState1));
				}
			}
		}
		return retVal;
	}
}
