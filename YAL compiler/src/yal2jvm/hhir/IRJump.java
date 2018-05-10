package yal2jvm.hhir;

import java.util.ArrayList;

public class IRJump extends IRNode
{
	private String label;
	
	public IRJump(String label)
	{
		this.label = label;
	}
	
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.add("goto " + label);
		return inst;
	}
}
