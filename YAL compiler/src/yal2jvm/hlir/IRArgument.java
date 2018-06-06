package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *	IRArgument class that extend IRNode class
 */
public class IRArgument extends IRNode
{
	private int register;

	/**
	 * TODO
	 * @param register
	 */
	public IRArgument(int register)
	{
		this.register = register;
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		return null;
	}

	/**
	 * Returns the value of the field register
	 * @return	value of the field register
	 */
	public int getRegister()
	{
		return register;
	}
}
