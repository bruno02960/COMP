package yal2jvm.hlir;

import java.util.ArrayList;

public class IRJump extends IRNode
{
	private String label;
	
	public IRJump(String label)
	{
		this.label = label;
		this.setNodeType("Jump");
	}
	
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.add("goto " + label);
		return inst;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
