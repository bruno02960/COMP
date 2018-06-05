package yal2jvm.hlir.register_allocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import yal2jvm.Yal2jvm;
import yal2jvm.hlir.liveness_analysis.IntGraph;

/**
 *
 */
public class RegisterAllocator
{
	private HashMap<String, IntGraph> intGraphs;
	private HashMap<String, HashMap<String, Integer>> allocatedRegisterByMethodName = new HashMap<>();

	/**
	 *
	 * @param intGraphs
	 */
	public RegisterAllocator(HashMap<String, IntGraph> intGraphs)
	{
		this.intGraphs = intGraphs;
	}

	/**
	 *
	 * @param numberRegisters
	 * @return
	 */
	public boolean allocate(int numberRegisters)
	{
		if (Yal2jvm.VERBOSE)
		{
			System.out.println("Doing register allocation for each method\n");
		}
		
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
				if (Yal2jvm.VERBOSE)
					System.out.println("Successfull register allocation with a maximum of " + numberRegisters + " registers for method " + methodName);
				allocatedRegisterByMethodName.put(methodName, graphColoring.getVarNameToRegisterNumber());
			}
		}

		return true;
	}

	/**
	 *
	 * @param graphColoring
	 * @param currNumberOfRegisters
	 * @return
	 */
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

	/**
	 *
	 * @return
	 */
	public HashMap<String, HashMap<String, Integer>> getAllocatedRegisterByMethodName()
	{
		return allocatedRegisterByMethodName;
	}

}
