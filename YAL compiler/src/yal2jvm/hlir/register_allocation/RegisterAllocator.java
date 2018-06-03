package yal2jvm.hlir.register_allocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import yal2jvm.hlir.liveness_analysis.IntGraph;

public class RegisterAllocator
{
	private HashMap<String, IntGraph> intGraphs;
	private HashMap<String, HashMap<String, Integer>> allocatedRegisterByMethodName = new HashMap<>();

	public RegisterAllocator(HashMap<String, IntGraph> intGraphs)
	{
		this.intGraphs = intGraphs;
	}
	
	public boolean allocate(int numberRegisters)
	{
		Iterator it = intGraphs.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			String methodName = (String) pair.getKey();
			GraphColoring graphColoring = new GraphColoring((IntGraph) pair.getValue(), numberRegisters);
			if(graphColoring.colorGraph() == false)
			{
				System.out.println("Error allocating registers to method " + methodName + ".");
				int numRegisterThatAllowToAllocate = findNumberOfRegisterThatAllowToAllocate(graphColoring, numberRegisters);
				System.out.println("Number of registers must be equal or higher than " + numRegisterThatAllowToAllocate + ".");
				return false;
			}
			else
			{
				System.out.println("Successfull register allocation with max. " + numberRegisters + " registers for method " + methodName);
				allocatedRegisterByMethodName.put(methodName, graphColoring.getVarNameToRegisterNumber());
			}
		}

		return true;
	}

	private int findNumberOfRegisterThatAllowToAllocate(GraphColoring graphColoring, int currNumberOfRegisters)
	{
		do
		{
			currNumberOfRegisters++;
			graphColoring.setNumRegisters(currNumberOfRegisters);
		}
		while(graphColoring.colorGraph() == false);

		return currNumberOfRegisters;
	}

	public HashMap<String, HashMap<String, Integer>> getAllocatedRegisterByMethodName()
	{
		return allocatedRegisterByMethodName;
	}

}
