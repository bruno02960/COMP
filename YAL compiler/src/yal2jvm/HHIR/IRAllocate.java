package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{

	private String name;
	private Type type;

	public IRAllocate(String name, Type type)
	{
		this.nodeType = "Allocate";
		this.name = name;
		this.type = type;
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
