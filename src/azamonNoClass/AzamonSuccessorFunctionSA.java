package azamonNoClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class AzamonSuccessorFunctionSA implements SuccessorFunction {
	private Paquetes p;
	private Transporte t;
	
	public AzamonSuccessorFunctionSA(Paquetes p, Transporte t) {
		this.p = p;
		this.t = t;
	}
	
	public List getSuccessors(Object aState) {
		ArrayList retVal = new ArrayList();
		AzamonState state = (AzamonState) aState;
		Random myRandom = new Random();
		boolean end = false;
		while (!end) {
			if (myRandom.nextInt(2) % 2 == 0) {
				int i = myRandom.nextInt(p.size());
				int j;
				do j=myRandom.nextInt(p.size());
				while (i==j);
				if (state.checkSwap(i, j)) {
					AzamonState newState = new AzamonState(state.getState());
					newState.swapPckts(i, j);
					retVal.add(new Successor(new String("Swapped packet: " + i + " for " + j+"  Offer " + state.getOferta(i) + " <-> " + state.getOferta(j)),newState));
					end = true;
				}
			}
			else {
				int pckt = myRandom.nextInt(p.size());
				int offer = myRandom.nextInt(t.size());
				
				if (state.checkSelectOffer(pckt, offer)) {
					AzamonState newState1 = new AzamonState(state.getState());
					newState1.selectOffer(pckt, offer);
					retVal.add(new Successor(new String("Selected offer: " + offer + " for packet" + pckt),newState1));
					end = true;
				}
			}
		}
		return retVal;
	}

}
