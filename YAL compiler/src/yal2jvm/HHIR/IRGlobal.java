package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{

	private String name;
	private Type type;
	private Integer initVal;

	public IRGlobal(String name, Type type, Integer initVal)
	{
		this.name = name;
		this.type = type;
		this.initVal = initVal;
		this.nodeType = "Global";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
