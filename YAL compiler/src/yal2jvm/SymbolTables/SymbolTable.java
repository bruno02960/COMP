package yal2jvm.SymbolTables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable
{
    private HashMap<String, VarSymbol> nameToSymbol = new HashMap<String, VarSymbol>();

    public SymbolTable(){}
    
    public void addSymbolAndSymbolName(VarSymbol varSymbol)
    {
        String uniqueIdentifier = varSymbol.getId() + "-" + varSymbol.getId();
        nameToSymbol.put(uniqueIdentifier, varSymbol);
    }

    public VarSymbol getSymbolBySymbolIdAndType(String symbolId, String symbolType)
    {
        String uniqueIdentifier = symbolId + "-" + symbolType;
        return nameToSymbol.get(uniqueIdentifier);
    }

    public ArrayList<VarSymbol> getSymbolBySymbolId(String symbolId)
    {
        ArrayList<VarSymbol> varSymbols = new ArrayList<>();
        Iterator it = nameToSymbol.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(((String)pair.getKey()).contains(symbolId + "-"))
                varSymbols.add((VarSymbol)pair.getValue());
        }
        return varSymbols;
    }
}
