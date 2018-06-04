package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRLabel extends IRNode
{
	private String label;

	/**
	 *
	 * @param label
	 */
	public IRLabel(String label)
	{
		this.label = label;
		this.setNodeType("Label");
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.add(label + ":");
		return inst;
	}

	/**
	 *
	 * @return
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 *
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
}
