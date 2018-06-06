package yal2jvm.hlir;

import java.util.ArrayList;

/**
 * TODO
 */
public class IRJump extends IRNode
{
	private String label;

	/**
	 * TODO
	 * @param label
	 */
	public IRJump(String label)
	{
		this.label = label;
		this.setNodeType("Jump");
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.add("goto " + label);
		return inst;
	}

	/**
	 * returns the value of the field Label
	 * @return value of field Label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the value of the field Label to the value of the parameter label
	 * @param label new value for the field Label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
}
