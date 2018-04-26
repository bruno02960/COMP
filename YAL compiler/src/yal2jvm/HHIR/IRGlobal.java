package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{

	private String name;
	private Type type;
	private Integer initVal;
	private Integer size;

	/**
	 * Used for global variables of type integer
	 * @param name	integer name
	 * @param type	integer type
	 * @param initVal	integer initial value
	 */
	public IRGlobal(String name, Type type, Integer initVal)
	{
		this.name = name;
		assert type==Type.INTEGER;
		this.type = type;
		this.initVal = initVal;
		this.nodeType = "Global";
	}

	/**
	 * Used for global variables of type array
	 * @param name	array name
	 * @param type	array type
	 * @param initVal	array initial value
	 * @param size	array size
	 */
	public IRGlobal(String name, Type type, Integer initVal, Integer size)
	{
		this.name = name;
		assert type==Type.ARRAY;
		this.type = type;
		this.initVal = initVal;
		this.nodeType = "Global";
		this.size = size;
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
}
