package yal2jvm.SymbolTables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable
{
    private HashMap<String, Symbol> nameToSymbol = new HashMap<String, Symbol>();

    public SymbolTable(){}
    
    public void addSymbolAndSymbolName(Symbol symbol)
    {
        String uniqueIdentifier = symbol.getId() + "-" + symbol.getId();
        nameToSymbol.put(uniqueIdentifier, symbol);
    }

    public Symbol getSymbolBySymbolIdAndType(String symbolId, String symbolType)
    {
        String uniqueIdentifier = symbolId + "-" + symbolType;
        return nameToSymbol.get(uniqueIdentifier);
    }

    public ArrayList<Symbol> getSymbolBySymbolId(String symbolId)
    {
        ArrayList<Symbol> symbols  = new ArrayList<>();
        Iterator it = nameToSymbol.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(((String)pair.getKey()).contains(symbolId + "-"))
                symbols.add((Symbol)pair.getValue());
        }
        return symbols;
    }
}
