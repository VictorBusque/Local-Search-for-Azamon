package azamonNoClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import IA.Azamon.*;

public class AzamonState {
	
	private static Transporte t;
	private static Paquetes p;
	
	private Integer[] packOferta;
	
	public AzamonState(Integer[] packs) {
		packOferta = packs;
	}
		
	public AzamonState(Paquetes pa, Transporte tr, int seed){//NO RANDOM
		p = pa;
		t = tr;
		packOferta = new Integer[p.size()];
				
		//Estructuras Locales para no crearla en cada llamada
		ArrayList<Integer> p0 = new ArrayList<Integer>();
		ArrayList<Integer> p01 = new ArrayList<Integer>();
		ArrayList<Integer> p012 = new ArrayList<Integer>();
		for (int o=0;o<t.size();++o){
			switch (t.get(o).getDias()){
				case(1):p0.add(o);
				case(2):
				case(3):p01.add(o);
				case(4):
				case(5):p012.add(o);
				break;
			}
		}
		
		boolean Random = seed<0;
		double weight_limit = 10;
		while (weight_limit >= 0) {
			for (int i = 0; i < p.size(); i++) {
				if (p.get(i).getPeso() >= weight_limit && packOferta[i] == null) {
					int pck = i;
					boolean set = false;
					
					ArrayList<ArrayList<Integer> > struct =new ArrayList<ArrayList<Integer> >();
					for(int j=0;j<p.size();++j) struct.add(new ArrayList<Integer>());
					
					while (!set) {
						int offr;
						if(Random)offr = calculaOf(pck,p0,p01,p012);
						else offr = calculaOf(pck,p0,p01,p012,seed);
						
						set=(offr!=-1);
						if (set) packOferta[pck] = offr;
						else{
							int packC;
							if(Random)packC = packtChange(pck);
							else packC = packtChange(pck,struct,seed);
							
							int off = packOferta[packC];
							packOferta[packC] = null;
							packOferta[pck] = off;
							pck = packC;
						}
					}
				}
			}
			weight_limit--;
		}
	}
	
	private boolean evalue(int pcktAct, int RandPack){//cert si es pot canviar (en un estat NO Solucio) cridat en la creadora
		if(RandPack == pcktAct || packOferta[RandPack] == null) return false;
		if(p.get(pcktAct).getPrioridad() < t.get(packOferta[RandPack]).getDias()/2) return false;
		return ((getPesoOferta(packOferta[RandPack]) - p.get(RandPack).getPeso() + p.get(pcktAct).getPeso()) 
				< t.get(packOferta[RandPack]).getPesomax());
	}
	
	private int packtChange(int pcktAct, ArrayList<ArrayList<Integer>> struct, int seed) {
		int size = p.size();
		Integer swapPack=seed=(seed%size);
		while (swapPack<(seed+size) &&   (struct.get(pcktAct).contains(swapPack%size)||!evalue(pcktAct,swapPack%size))) swapPack++;
		struct.get(pcktAct).add(swapPack%size);
		return swapPack%size;
	}
	private int packtChange(int pcktAct) {
		int RandPack;
		do RandPack = new Random().nextInt(p.size());
		while (!evalue(pcktAct,RandPack));
		return RandPack;
	}
	
	private int calculaOf(int pckt, ArrayList<Integer> p0, ArrayList<Integer> p01, ArrayList<Integer> p012, int seed){//igual
		ArrayList<Integer> aux = new ArrayList<Integer>();
		switch (p.get(pckt).getPrioridad()) {
			case(0):aux = p0;  break;
			case(1):aux = p01; break;
			case(2):aux = p012;break;
		}
		int size = aux.size();
		int i=seed=(seed%size);
		while(i < (seed+size) && !checkSelectOffer(pckt,aux.get(i%size)))i++;
		if(i == (seed+size))return -1;
		else return aux.get(i%size);
	}
	private int calculaOf(int pckt, ArrayList<Integer> p0, ArrayList<Integer> p01, ArrayList<Integer> p012){//igual
		ArrayList<Integer> aux = new ArrayList<Integer>();
		switch (p.get(pckt).getPrioridad()) {
			case(0):aux = p0;  break;
			case(1):aux = p01; break;
			case(2):aux = p012;break;
		}
		Collections.shuffle(aux);
		int i=0;
		while(i < aux.size() && !checkSelectOffer(pckt,aux.get(i)))i++;
		if(i == aux.size())return -1;
		else return aux.get(i);
	}

	
	public boolean checkSetup() {
		for (int of = 0; of < t.size(); of++) {
			if (getPesoOferta(of) > t.get(of).getPesomax()) return false;
		}
		for (int pck = 0; pck < p.size(); pck++) {
			switch(p.get(pck).getPrioridad()) {
				case(0):return (!(t.get(packOferta[pck]).getDias() > 1));
				case(1):return (!(t.get(packOferta[pck]).getDias() > 3));
				case(2):return true;
			}
		}
		return false;
	}
	/////////////////////////////////////////////////////////////////////////////////	
	
	//[0]: cost  [1]: sat
	public ArrayList<Double> heuristicCalc(){//control de heuristica
		double satisfaction = 0;
		double costTotal = 0;
		for(int pckt = 0;pckt < p.size(); ++pckt){
			int offr = packOferta[pckt];
			switch (p.get(pckt).getPrioridad()) {
				case (1):
					if (t.get(offr).getDias() == 1) satisfaction += 1;
					break;
				case (2):
					if (t.get(offr).getDias() == 1) satisfaction += 3;
					else if (t.get(offr).getDias() == 2) satisfaction += 2;
					else if (t.get(offr).getDias() == 3) satisfaction += 1;
					break;
			}
			
			double costs = p.get(pckt).getPeso()*t.get(offr).getPrecio();//we get the transport cost
			if (t.get(offr).getDias() == 5) costs += 2*0.25*p.get(pckt).getPeso(); //adding the 2-day store cost
			else if (t.get(offr).getDias() == 3 || t.get(offr).getDias() == 4) costs += 0.25*p.get(pckt).getPeso();	 //and the 3-4 day
			costTotal -=costs;			
		}
				
		ArrayList<Double> ret = new ArrayList<Double>();
		ret.add(((int)(costTotal*100))/100.0);
		ret.add(satisfaction);
		return ret;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	private double getPesoOferta(int i) {
		double peso = 0;
		for (int j = 0; j < packOferta.length; j++) {
			if (packOferta[j] != null && packOferta[j] == i) peso += p.get(j).getPeso(); 
		}
		return peso;
	}
	
	private boolean checkAsignacion(int pckt, int offr) {
		switch(p.get(pckt).getPrioridad()) {
			case(0):return (t.get(offr).getDias() == 1);
			case(1):return (t.get(offr).getDias() <= 3);
			case(2):return true;
			default:return false;
		}
	}
		
	public boolean checkSwap(int i, int j) {
		if ((packOferta[i] != null && packOferta[j] != null) && (packOferta[i] == packOferta[j])) return false;
		if ((getPesoOferta(packOferta[i]) - p.get(i).getPeso() + p.get(j).getPeso()) >= t.get(packOferta[i]).getPesomax()) return false;
		if ((getPesoOferta(packOferta[j]) - p.get(j).getPeso() + p.get(i).getPeso()) >= t.get(packOferta[j]).getPesomax()) return false;
		return (checkAsignacion(i,packOferta[j]) && checkAsignacion(j,packOferta[i]));
	}
	
	public boolean checkSelectOffer(int pckt, int offr) {
		if (packOferta[pckt] != null && packOferta[pckt] == offr) return false;
		if ((getPesoOferta(offr) + p.get(pckt).getPeso()) >= t.get(offr).getPesomax()) return false;
		return checkAsignacion(pckt, offr);
	}
	
	public void swapPckts(int i, int j) {
		int aux = new Integer(packOferta[i]);
		int aux1 = new Integer(packOferta[j]);
		packOferta[i] = aux1;
		packOferta[j] = aux;
	}
	
	public void selectOffer(int pckt, int offr) {
		packOferta[pckt] = offr;
	}
	 
	public Integer[] getState() {
		Integer[] a = new Integer[packOferta.length];
		for (int i = 0; i < packOferta.length; i++) a[i] = packOferta[i];
		return a;
	}
	
	public int getOferta(int pckt) {
		return packOferta[pckt];
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < p.size(); i++) {
			buf.append("Paquete: "+i+"	Prioridad: " + p.get(i).getPrioridad() + "	Oferta: "+ 
			packOferta[i]+ "	Dias entrega: " + 
			t.get(packOferta[i]).getDias() +"\n");
		}
		return buf.toString();
	}
}
