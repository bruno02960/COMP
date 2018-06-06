package yal2jvm.hlir;

/**
 * TODO
 */
public class VariableArray extends Variable
{
	Variable at;

	/**
	 * TODO
	 * @param var
	 * @param at
	 */
	VariableArray(String var, Variable at)
	{
		super(var, Type.ARRAY);
		this.at = at;
	}

	/**
	 * Returns the value of the field at
	 * @return	value of the field at
	 */
	Variable getAt()
	{
		return at;
	}
}
