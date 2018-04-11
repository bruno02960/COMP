package yal2jvm.SymbolTables;

import java.util.ArrayList;


public class Symbol
{
    private String name;
    private String type;
    private ArrayList<Integer> values = new ArrayList<Integer>(); //can be just one value or multiple if is an array

    public Symbol(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

}
