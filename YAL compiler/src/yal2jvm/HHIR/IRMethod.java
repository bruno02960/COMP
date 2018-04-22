package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRMethod extends IRNode
{
	private String name;
	private Type returnType;
	private Type[] argsType;
	public int labelN = 0;
	public int regN = 0;
	public int varN = 0;

	public IRMethod(String name, Type returnType, Type[] argsTypes)
	{
		this.name = name;
		this.returnType = returnType;
		this.argsType = argsTypes;
		this.argsType = argsTypes == null ? this.argsType = new Type[0] : argsTypes;
		this.nodeType = "Method";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		String inst1 = ".method public static ";
		
		if (name.equals("main"))
			inst1 += "main([Ljava/lang/String;)V";
		else
		{
			inst1 += name + "(";
			for (int i = 0; i < argsType.length; i++)
			{
				switch(argsType[i])
				{
					case INTEGER:
					{
						inst1 += "I";
						break;
					}
					case ARRAY: break;
					default: break;
				}
			}
			inst1 += ")";
			
			switch(returnType)
			{
				case INTEGER:
				{
					inst1 += "I";
					break;
				}
				case ARRAY: break;
				case VOID:
					inst1 += "V";
					break;
				default:
					break;
			}
		}
		
		ArrayList<String> methodBody = getMethodBody();
		String instFinal = ".end method";
		
		inst.add(inst1);
		inst.addAll(methodBody);
		inst.add(instFinal);
		return inst;
	}

	private ArrayList<String> getMethodBody()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		int localsCount = 0;
		for (int i = 0; i < children.size(); i++)
		{
			IRNode node = children.get(i);
			if (node.toString() == "Allocate")
				localsCount++;
		}
		if (localsCount > 0)
			inst.add(".limit locals " + localsCount);
		
		for (int i = 0; i < children.size(); i++)
		{
			IRNode node = children.get(i);
			inst.addAll(node.getInstructions());
		}
		return inst;
	}
}
