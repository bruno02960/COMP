package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.HashMap;

import yal2jvm.hlir.IRMethod;
import yal2jvm.hlir.IRModule;
import yal2jvm.hlir.IRNode;

/**
 *
 */
public class LivenessAnalyzer 
{
	private IRModule ir;
	private HashMap<String,IntGraph> intGraphs;

	/**
	 *
	 * @param ir
	 */
	public LivenessAnalyzer(IRModule ir)
	{
		this.ir = ir;
		this.intGraphs = new HashMap<>();
	}

	/**
	 *
	 */
	public void analyze()
	{
		ArrayList<IRNode> children = ir.getChildren();
		for (IRNode n : children)
		{
			if (n.getNodeType().equals("Method"))
			{
				IRMethod method = (IRMethod)n;
				SetBuilder met = new SetBuilder(method);
				MethodAnalyzer analyzer = new MethodAnalyzer(met);
				analyzer.analyze();
				this.intGraphs.put(method.getName(), analyzer.getGraph());
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public HashMap<String,IntGraph> getInterferenceGraphs()
	{
		return intGraphs;	
	}
}
