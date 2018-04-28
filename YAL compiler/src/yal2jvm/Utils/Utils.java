package yal2jvm.Utils;

import yal2jvm.SymbolTables.Symbol;

import java.util.HashMap;
import java.util.Map;

public class Utils
{

    public static HashMap<String, Symbol> copyHashMap(HashMap<String, Symbol> original)
    {
        HashMap<String, Symbol> copy = new HashMap<String, Symbol>();
        for (Map.Entry<String, Symbol> entry : original.entrySet())
            copy.put(entry.getKey(), entry.getValue().getCopy());

        return copy;
    }
}
