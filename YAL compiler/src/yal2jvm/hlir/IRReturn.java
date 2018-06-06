package yal2jvm.hlir;

import java.util.ArrayList;

/**
 * TODO
 */
public class IRReturn extends IRNode
{
	private String name;
	private Type type;

	/**
	 * TODO
	 * @param returnVar
	 */
	public IRReturn(Variable returnVar)
	{
		this.name = returnVar.getVar();
		this.type = returnVar.getType();
		this.setNodeType("Return");
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		if (type == Type.VOID)
			inst.add("return");
		else
		{
			IRLoad irLoad = new IRLoad(name, type);
			addChild(irLoad);
			inst.addAll(irLoad.getInstructions());
			if (type == Type.ARRAY)
				inst.add("areturn");
			else
				inst.add("ireturn");
		}

		return inst;
	}

	/**
	 * Returns the value of the field name
	 * @return value of the field name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the value of the field name to the value of the parameter name
	 * @param name	new value for the field name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}
