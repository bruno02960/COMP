package yal2jvm.ast;

import yal2jvm.SymbolTables.Symbol;

import java.util.ArrayList;

public class FunctionSymbol
{
    private SimpleNode functionAST;
    private String name;
    private ArrayList<Symbol> arguments;
    private Symbol returnValue;

    public FunctionSymbol(SimpleNode functionAST, String name, ArrayList<Symbol> arguments, Symbol returnValue)
    {
        this.functionAST = functionAST;
        this.name = name;
        this.arguments = arguments;
        this.returnValue = returnValue;
    }

    public SimpleNode getFunctionAST()
    {
        return functionAST;
    }

    public void setFunctionAST(SimpleNode functionAST)
    {
        this.functionAST = functionAST;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<Symbol> getArguments()
    {
        return arguments;
    }

    public void setArguments(ArrayList<Symbol> arguments)
    {
        this.arguments = arguments;
    }

    public Symbol getReturnValue()
    {
        return returnValue;
    }

    public void setReturnValue(Symbol returnValue)
    {
        this.returnValue = returnValue;
    }
}
