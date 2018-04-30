package yal2jvm.symbol_tables;

public class ImmediateSymbol extends VarSymbol
{

    public ImmediateSymbol(Integer value)
    {
        super(value.toString(), SymbolType.INTEGER.toString(), true);
    }

    public ImmediateSymbol(String id)
    {
        super(id, SymbolType.INTEGER.toString(), true);
    }

}
