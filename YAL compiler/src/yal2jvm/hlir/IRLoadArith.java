package yal2jvm.hlir;

import java.util.ArrayList;

/**
 * TODO
 */
public class IRLoadArith extends IRNode
{
	private IRArith irArith;

	/**
	 * TODO
	 * @param op
	 */
	public IRLoadArith(Operation op)
	{
		this.setNodeType("LoadArith");
		irArith = new IRArith(op);
		this.addChild(irArith);
	}

	/**
	 * TODO
	 * @return
	 */
	public IRNode getRhs()
	{
		return irArith.getRhs();
	}

	/**
	 * TODO
	 * @param rhs
	 */
	public void setRhs(IRNode rhs)
	{
		this.irArith.setRhs(rhs);
	}

	/**
	 * TODO
	 * @return
	 */
	public IRNode getLhs()
	{
		return irArith.getLhs();
	}

	/**
	 * TODO
	 * @param lhs
	 */
	public void setLhs(IRNode lhs)
	{
		this.irArith.setLhs(lhs);
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		return irArith.getInstructions();
	}

}
