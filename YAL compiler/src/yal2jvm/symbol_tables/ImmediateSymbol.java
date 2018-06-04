package yal2jvm.symbol_tables;

/**
 *
 */
public class ImmediateSymbol extends VarSymbol
{
    /**
     *
     * @param value
     */
    public ImmediateSymbol(Integer value)
    {
        super(value.toString(), SymbolType.INTEGER.toString(), true);
    }

    /**
     *
     * @param id
     */
    public ImmediateSymbol(String id)
    {
        super(id, SymbolType.INTEGER.toString(), true);
    }

}
