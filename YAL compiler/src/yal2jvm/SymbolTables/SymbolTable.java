package yal2jvm.SymbolTables;

import java.util.HashMap;

public class SymbolTable
{
    private HashMap<String, Symbol> nameToSymbol = new HashMap<String, Symbol>();

    public SymbolTable(){}
    
    public void addSymbolAndSymbolName(Symbol symbol)
    {
        nameToSymbol.put(symbol.getName(), symbol);
    }

    public Symbol getSymbolBySymbolName(String symbolName)
    {
        return nameToSymbol.get(symbolName);
    }
}
