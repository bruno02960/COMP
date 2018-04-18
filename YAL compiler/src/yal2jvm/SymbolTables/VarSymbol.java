package yal2jvm.SymbolTables;

import yal2jvm.ast.Symbol;

import java.util.ArrayList;


public class VarSymbol extends Symbol
{
    private String type;
    private ArrayList<Integer> values; //can be just one value or multiple if is an array

    public VarSymbol(String id, String type)
    {
        super(id);
        this.type = type;
    }

    public VarSymbol(String id, String type, ArrayList<Integer> values)
    {
        super(id);
        this.type = type;
        this.values = values;
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
