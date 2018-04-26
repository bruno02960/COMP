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
		
		String inst1 = ".field private static " + name;
		switch(type)
		{
			case INTEGER:
			{
				inst1 += " I ";
				if (initVal != null)
					inst1 += "= " + initVal;
				break;
			}
			case ARRAY: break;
			default: break;
		}
		
		inst.add(inst1);
		return inst;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public Type getType() 
	{
		return this.type;
	}
}
