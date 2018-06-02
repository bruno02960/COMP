package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodAnalyzer
{
	private ArrayList<Line> lines;
	private HashMap<Integer, String> vars;
	private Method method;
	private String methodName;
	
	public MethodAnalyzer(Method method)
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
		System.out.print("Local vars: ");
		System.out.println(allVars);
	}

	public IntGraph getGraph()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
