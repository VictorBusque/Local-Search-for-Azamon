package azamonNoClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class AzamonDemo {
	//Constantes para facilitar la inicializacion de los parametros por codigo
	final static int	 RandS 	= -1;
	final static int	 ConsS 	=  0;
	final static boolean CS 	= true;
	final static boolean C 		= false;
	final static boolean SA 	= true;
	final static boolean HC 	= false;
	
	//Parametros de programa
	static int 		seedPO;				//Semilla para paquetes y ofertas
	static int		size;				//Numero de paquetes
	static double	rate;				//Proporcion oferta/paquete
	static int 		seedState;			//Semilla para el estado inicial
	static boolean 	heuristic;			//Heuristico a usar
	static boolean 	typeSearch;			//Tipo de busqueda a realizar
							//Default values
	static int 		steps 	= 10000;	//Numero maximo de iteraciones
	static int 		stiter	= 100;		//Afecta a la probabilidad de aceptacion de un nodo peor
	static int 		k 		= 20;		//Mayor k, mas tarda a decrementar
	static double 	lamb	= 0.005;	//Mayor lamb, mas rapido desciende
	
	static Paquetes p;
	static Transporte t;
	
	static long timeIni,time;
	
	static AzamonState iniState;
	static AzamonState goalState;
	
	static Scanner input = new Scanner(System.in);
	
	private static void printInstrumentation(Properties properties) {
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			System.out.println(key + " : " + property);
		}System.out.println();
	}
	private static void printActions(List actions) {
		for (int i = 0; i < actions.size(); i++) {
			String action = (String) actions.get(i).toString();
			System.out.println(action);
		}System.out.println();
	}
	private static void printInicialState() {
		System.out.println("Estado de la Solucion Inicial:");
		System.out.println(iniState);
		if(iniState.checkSetup() ) System.out.println("Estado Inicial Correcto");
		else System.out.println("ERROR!!!!!!!El estado inicial no es solucion!");
		ArrayList<Double> h = iniState.heuristicCalc();
		System.out.println("Coste: "+h.get(0)+"	Satisfacion: "+(int)(double)h.get(1));
		System.out.println();
	}
	private static void printGoalState() {
		System.out.println("Estado de la Solucion Final:");
		System.out.println(goalState);
		if(goalState.checkSetup()) System.out.println("Estado Final Correcto");
		else System.out.println("ERROR!!!!!!!El estado final no es solucion!");
		ArrayList<Double> h = goalState.heuristicCalc();
		System.out.println("Coste: "+h.get(0)+"	Satisfacion: "+(int)(double)h.get(1));
		System.out.println();
	}
	private static void printPackTrans(){
		System.out.println("Paquetes generados:");
		System.out.println(p);
		System.out.println();
		System.out.println("Ofertas generadas:");
		System.out.println(t);
		System.out.println();
	}
	private static void printParams(){
		System.out.println("Parametros usados:");
		System.out.println("Seed:		"+seedPO);
		System.out.println("Size:		"+size);
		System.out.println("Rate:		"+rate);
		if(seedState<0) System.out.println("Inicial State:	Random");
		else 		 	System.out.println("Inicial State:	"+seedState);
		if(heuristic)	System.out.println("Heuristic: 	Cost and Satisfaction");
		else			System.out.println("Heuristic: 	Cost");
		if(!typeSearch)	System.out.println("Type Search: 	Hill Climbing");
		else{
			System.out.println("Type Search: 	Simulated Annealing");
			System.out.println();
			System.out.println("Steps:		"+steps);
			System.out.println("StIter:		"+stiter);
			System.out.println("K:		"+k);
			System.out.println("Lambda:		"+lamb);
		}
		System.out.println();
	}
	private static void printResumResult(){
		if(goalState.checkSetup()) System.out.println("Estado Final Correcto");
		else System.out.println("ERROR!!!!!!!El estado final no es solucion!");
		ArrayList<Double> h = goalState.heuristicCalc();
		System.out.println("Coste: "+h.get(0)+"	Satisfacion: "+(int)(double)h.get(1)+"	T: "+time+" ms");
		System.out.println();
	}
	
	//Parameter iniStateHC is the initialState
	private static void AzamonHC(AzamonState iniStateHC, HeuristicFunction HF) throws Exception {
		Problem problem = new Problem(iniStateHC, 
						  new AzamonSuccessorFunctionHC(p,t), 
						  new AzamonGoalTest(), HF);
		
		Search search = new HillClimbingSearch();
		SearchAgent agent = new SearchAgent(problem,search);
		postSearch(search,agent);
	}

	//Parameter iniStateSA is the initialState
	private static void AzamonSA(AzamonState iniStateSA, HeuristicFunction HF) throws Exception {
		Problem problem = new Problem(iniStateSA, 
						  new AzamonSuccessorFunctionSA(p,t), 
						  new AzamonGoalTest(), HF);
		
								// Default values :: 10000, 100, 20,0.005
		Search search = new SimulatedAnnealingSearch(steps,stiter,k,lamb);
		SearchAgent agent = new SearchAgent(problem,search);
		postSearch(search,agent);
	}
	
	private static void start() throws Exception{
		System.out.println("Calculando ...");
		System.out.println();
		
		if (seedPO==-1)seedPO = new Random().nextInt(10000);
		p = new Paquetes(size, seedPO);
		t = new Transporte(p, rate, seedPO);
		HeuristicFunction hf;
		if(heuristic)	hf=new AzamonHeuristicFunctionCS();
		else 			hf=new AzamonHeuristicFunctionC();
		
		timeIni=System.currentTimeMillis();	
		if(typeSearch)	{iniState = new AzamonState(p,t,seedState);AzamonSA(iniState,hf);}
		else 			{iniState = new AzamonState(p,t,seedState);AzamonHC(iniState,hf);}
	}
	
	private static void iniParams(){
		System.out.println("Inicializa los parametros con valores correctos:");
		
		System.out.println("Numero de paquetes:");
		size = input.nextInt();
		System.out.println("Proporcion de oferta/paquete (usar ',' para el decimal):");
		rate = input.nextDouble();
		System.out.println("Semilla de los paquetes y transporte: [-1 para semilla aleatoria]");
		seedPO = input.nextInt();
		
		System.out.println("Semilla del estado inicial: [-1 para estado inicial random]");
		seedState = input.nextInt();
		System.out.println("Heuristico: [0: Minimiza Costes || 1: Minimiza Costes y Maximiza Satisfacion]");
		heuristic = input.nextInt()==1;
		System.out.println("Tipo de Busqueda: [0: Hill Climbing	|| 1: Simulated Annealing]");
		typeSearch = input.nextInt()==1;
		
		if(typeSearch){	
			System.out.println("Numero maximo de iteraciones (steps):");
			steps  = input.nextInt();
			System.out.println("El numero de iteraciones por cada paso de temperatura (stiter)");
			stiter	 = input.nextInt();
			System.out.println("Los parametros que determinan el comportamiento de la funcion de temperatura:");
			System.out.print("k: ");
			k  = input.nextInt();
			System.out.print("lambda (usar ',' para el decimal): ");
			lamb = input.nextDouble();
		}
		System.out.println("Inicializacion de parametros finalizada");
	}
	
	private static void postSearch(Search search, SearchAgent agent){
		time = (System.currentTimeMillis()-timeIni);
		System.out.println("Calculo finalizado");
		goalState = (AzamonState) search.getGoalState();
		
		boolean fi = false;
		while(!fi){
			System.out.println("Que quieres hacer?");
			System.out.println("1: Mostrar Estado Inicial");
			System.out.println("2: Mostrar Estado Final");
			System.out.println("3: Mostrar Parametros usados");
			System.out.println("4: Mostrar Resultado Resumido");
			System.out.println("5: Mostrar Paquetes y Ofertas");
			System.out.println("6: Mostrar Acciones (printActions)");
			System.out.println("7: Mostrar Instrumentacion (printInstrumentation)");
			System.out.println("8: Acabar");
			int op = input.nextInt();
			switch(op){
				case 1: printInicialState();								break;
				case 2: printGoalState();									break;
				case 3: printParams();										break;
				case 4: printResumResult();									break;
				case 5: printPackTrans();									break;
				case 6: printActions(agent.getActions());					break;
				case 7: printInstrumentation(agent.getInstrumentation());	break;
				case 8: fi=true;											break;	
				default: System.out.println(op+" no es una opcion valida");	break;
			}
		}
		input.close();
	}
	
	public static void main(String[] args) throws Exception {
		iniParams();
		start();
	}
	
}
