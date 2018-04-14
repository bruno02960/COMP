package yal2jvm.SymbolTables;

import java.util.ArrayList;


public class Symbol
{
    private String id;
    private String type;
    private ArrayList<Integer> values; //can be just one value or multiple if is an array

    public Symbol(String id, String type)
    {
        this.id = id;
        this.type = type;
    }

    public Symbol(String id, String type, ArrayList<Integer> values)
    {
        this.id = id;
        this.type = type;
        this.values = values;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public ArrayList<Integer> getValues()
    {
        return values;
    }

    public void setValues(ArrayList<Integer> values)
    {
        this.values = values;
    }
}
