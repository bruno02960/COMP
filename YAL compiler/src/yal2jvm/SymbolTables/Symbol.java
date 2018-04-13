package yal2jvm.SymbolTables;

import java.util.ArrayList;


public class Symbol
{
    private String name;
    private String type;
    private ArrayList<Integer> values; //can be just one value or multiple if is an array

    public Symbol(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

    public Symbol(String name, String type, ArrayList<Integer> values)
    {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }
}
