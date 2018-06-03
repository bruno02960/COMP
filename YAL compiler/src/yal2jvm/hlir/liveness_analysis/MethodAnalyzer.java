package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;

public class MethodAnalyzer
{
	private MethodSetBuilder method;
	private String methodName;
	
	public MethodAnalyzer(MethodSetBuilder method)
	{
		this.method = method;
		this.methodName = method.getName();
		analyze();
	}
	
	public void analyze()
	{
		method.getAllVars();
		
		System.out.println("Liveness analysis of method " + method.getName());
		//System.out.print("Local vars: ");
		//System.out.println(allVars);
		
		method.buildAllLines();
		method.calculateSets();
		
		
		ArrayList<Line> lines = method.getLines();
		for (Line l : lines)
			System.out.println(l);
		System.out.println("");
		
	}

	public IntGraph getGraph()
	{
		ArrayList<IntPair> interferences = method.getAllPairs();
		
		IntGraph graph = new IntGraph();
		for (IntPair pair : interferences)
			graph.addInterference(pair.getVar1(), pair.getVar2());
		
		System.out.println(graph.toString());
		
		return graph;
	}
}
