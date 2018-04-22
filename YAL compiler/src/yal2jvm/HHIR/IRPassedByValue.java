package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRPassedByValue extends IRNode
{

	private String name;
	private Type type;

	public IRPassedByValue(String name, Type type)
	{
		this.name = name;
		this.type = type;
		this.nodeType = "PassedByValue";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
