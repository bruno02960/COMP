package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRConstant extends IRNode
{
	int value;
	
	public IRConstant(int value)
	{
		this.value = value;
		this.nodeType = "Constant";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		String inst1 = "ldc " + value;
	
		inst.add(inst1);
		return inst;
	}
}
