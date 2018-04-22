package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRMethod extends IRNode
{
	private String name;
	private Type returnType;
	private Type[] argsType;

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
		
		String inst1 = ".method public static " + name + "(";
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
		
		//ArrayList<String> methodBody = children.get(0).getInstructions();
		String instFinal = ".end method";
		
		inst.add(inst1);
		//inst.add(methodBody);
		inst.add(instFinal);
		return inst;
	}
}
