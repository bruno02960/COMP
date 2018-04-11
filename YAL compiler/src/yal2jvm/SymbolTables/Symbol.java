package yal2jvm.SymbolTables;

import java.util.ArrayList;


public class Symbol
{
    private String id;
    private String type;
    private ArrayList<Integer> values = new ArrayList<Integer>(); //can be just one value or multiple if is an array

    public Symbol(String id, String type) 
    {
        this.id = id;
        this.type = type;
    }

}
