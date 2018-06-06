package yal2jvm.symbol_tables;

/**
 * ImmediateSymbol class that extends the class Symbol
 */
public class ImmediateSymbol extends VarSymbol
{
	/**
	 * Constructor for the class ImmediateSymbol with value instead of id
	 * 
	 * @param value
	 *            value of the object
	 */
	public ImmediateSymbol(Integer value)
	{
		super(value.toString(), SymbolType.INTEGER.toString(), true);
	}

	/**
	 * Constructor for the class ImmediateSymbol with id instead of value
	 * 
	 * @param id
	 *            of the object
	 */
	public ImmediateSymbol(String id)
	{
		super(id, SymbolType.INTEGER.toString(), true);
	}

}
