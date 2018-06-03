package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class MethodAnalyzer
{
	private ArrayList<Line> lines;
	private HashMap<Integer, String> vars;
	private MethodSetBuilder method;
	private String methodName;
	
	public MethodAnalyzer(MethodSetBuilder method)
	{
		this.method = method;
		this.methodName = method.getName();
		this.lines = new ArrayList<>();
		analyze();
	}
	
	public void analyze()
	{
		ArrayList<String> allVars = method.getAllVars();
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
