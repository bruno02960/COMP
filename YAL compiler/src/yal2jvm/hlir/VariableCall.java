package yal2jvm.hlir;

/**
 * TODO
 */
public class VariableCall extends Variable
{
	IRCall irCall;

	/**
	 * TODO
	 * @param var
	 * @param type
	 * @param irCall
	 */
	VariableCall(String var, Type type, IRCall irCall)
	{
		super(var, type);
		this.irCall = irCall;
	}

	/**
	 * Returns the value of the field irCall
	 * @return	value of the field irCall
	 */
	IRCall getIrCall()
	{
		return irCall;
	}
}
