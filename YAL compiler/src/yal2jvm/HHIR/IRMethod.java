package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRMethod extends IRNode
{
	private String name;

	public IRMethod(String name)
	{
		this.name = name;
		this.nodeType = "Method";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}
}
