package yal2jvm;

import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.SimpleNode;

import java.util.HashMap;

public abstract class Analysis
{
    protected HashMap<String, Symbol> mySymbols;
    protected HashMap<String, Symbol> inheritedSymbols;
    protected SimpleNode ast;

    protected Analysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
        this.ast = ast;
        this.inheritedSymbols = inheritedSymbols;
        this.mySymbols = new HashMap<String, Symbol>();
    }

    protected abstract void parse();

    protected Symbol hasAccessToSymbol(String symbolId)
    {
        Symbol symbol;
        symbol = mySymbols.get(symbolId);
        if(symbol != null)
            return symbol;
        else
            symbol = inheritedSymbols.get(symbolId);

        return symbol;
    }
}
