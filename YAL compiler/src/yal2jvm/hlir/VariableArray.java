package yal2jvm.hlir;

/**
 *
 */
public class VariableArray extends Variable
{
	Variable at;

	/**
	 *
	 * @param var
	 * @param at
	 */
	VariableArray(String var, Variable at)
	{
		super(var, Type.ARRAY);
		this.at = at;
	}

	/**
	 *
	 * @return
	 */
	Variable getAt()
	{
		return at;
	}
}
