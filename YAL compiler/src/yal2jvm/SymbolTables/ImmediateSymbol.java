package yal2jvm.SymbolTables;


public class ImmediateSymbol extends VarSymbol
{
    public ImmediateSymbol(Integer value)
    {
        super(value.toString(), "INTEGER", true);
    }

    public ImmediateSymbol(String id)
    {
        super(id, "INTEGER", true);
    }

}
