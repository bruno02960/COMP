package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;

import yal2jvm.Yal2jvm;

/**
 *
 */
public class MethodAnalyzer
{
	private SetBuilder method;
	private String methodName;

	/**
	 *
	 * @param method
	 */
	public MethodAnalyzer(SetBuilder method)
	{
		this.method = method;
		this.methodName = method.getName();
	}

	/**
	 *
	 */
	public void analyze()
	{
		method.getAllVars();
		method.buildAllLines();
		method.calculateSets();
		getGraph();
		printResults();
	}
	
	private void printResults()
	{
		if (!Yal2jvm.VERBOSE)
			return;
		
		System.out.println("--------------------------------------------------");
		System.out.println("Liveness analysis of method " + methodName + ":\n");
		ArrayList<Line> lines = method.getLines();
		System.out.println("Local vars: " + method.getLocals() + "\n");
		for (Line l : lines)
			System.out.println(l);
		System.out.println("\nInterferences and mandatory registers:");
		System.out.println(getGraph().toString());
		System.out.println("--------------------------------------------------");
	}

	/**
	 *
	 * @return
	 */
	public IntGraph getGraph()
	{
		ArrayList<IntPair> interferences = method.getAllPairs();
		
		IntGraph graph = new IntGraph();
		for (IntPair pair : interferences)
			graph.addInterference(pair.getVar1(), pair.getVar2());
		
		ArrayList<String> locals = method.getLocals();
		for (String local : locals)
			graph.addVariable(local);
			
		ArrayList<String> args = method.getAllArgs();
		graph.setRequiredRegisters(args);
		
		return graph;
	}
}
