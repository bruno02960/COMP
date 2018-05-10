package yal2jvm.hhir;

import java.util.ArrayList;

public class IRLabel extends IRNode
{
	private String label;
	
	public IRLabel(String label)
	{
		this.label = label;
	}
	
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.add(label + ":");
		return inst;
	}
}
