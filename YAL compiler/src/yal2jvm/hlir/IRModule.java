package yal2jvm.hlir;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * TODO
 */
public class IRModule extends IRNode
{
	private String name;
	private int currLabelNumber = 1;

	/**
	 * TODO
	 * @param name
	 */
	public IRModule(String name)
	{
		super();
		this.setName(name);
		this.setNodeType("Module");
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();

		String inst1 = ".class public static " + name;
		String inst2 = ".super java/lang/Object";

		inst.add(inst1);
		inst.add(inst2);
		inst.add("\n");

		for (int i = 0; i < getChildren().size(); i++)
		{
			if (getChildren().get(i).toString().equals("Method"))
				inst.add("\n");
			inst.addAll(getChildren().get(i).getInstructions());
		}

		return inst;
	}

	/**
	 * Returns the value of the field name
	 * @return	value of the field name
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

	/**
	 * Returns the value of the field currLabelNumber incremented by one
	 * @return	the value of the field currLabelNumber plus one
	 */
	public int getAndIncrementCurrLabelNumber()
	{
		return currLabelNumber++;
	}

	/**
	 * TODO
	 * @param name
	 * @return
	 */
	public IRGlobal getGlobal(String name)
	{
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).toString().equals("Global"))
			{
				IRGlobal global = ((IRGlobal) children.get(i));
				if (global.getName().equals(name))
					return global;
			}
		}
		return null;
	}

	/**
	 * TODO
	 * @param name
	 * @return
	 */
	public IRMethod getChildMethod(String name)
	{
		for (int i = 0; i < children.size(); i++)
		{
			IRNode child = children.get(i);
			if (child instanceof IRMethod)
			{
				if (((IRMethod) child).getName().equals(name))
					return ((IRMethod) child);
			}
		}
		return null;
	}

	/**
	 * TODO
	 * @return
	 */
	public TreeSet<String> getAllGlobals()
	{
		TreeSet<String> globals = new TreeSet<>();
		for (IRNode i : children)
		{
			if (i.getNodeType().equals("Global"))
			{
				IRGlobal gl = (IRGlobal) i;
				globals.add(gl.getName());
			}
		}
		return globals;
	}
}
