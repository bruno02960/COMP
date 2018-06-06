package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRArgument extends IRNode
{
	private int register;

	/**
	 *
	 * @param register
	 */
	public IRArgument(int register)
	{
		this.register = register;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		return null;
	}

	/**
	 *
	 * @return
	 */
	public int getRegister()
	{
		return register;
	}
}
