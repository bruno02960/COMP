package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;

/**
 *
 */
public class MethodAnalyzer
{
	private MethodSetBuilder method;
	private String methodName;

	/**
	 *
	 * @param method
	 */
	public MethodAnalyzer(MethodSetBuilder method)
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
		
		System.out.println("Liveness analysis of method " + method.getName());
		//System.out.print("Local vars: ");
		//System.out.println(allVars);
		
		method.buildAllLines();
		method.calculateSets();
		
		//TODO remove
		ArrayList<Line> lines = method.getLines();
		for (Line l : lines)
			System.out.println(l);
		System.out.println("");
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
		
		System.out.println(graph.toString());
		
		return graph;
	}
}
