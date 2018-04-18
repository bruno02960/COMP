package yal2jvm.ast;

import yal2jvm.SymbolTables.VarSymbol;

import java.util.ArrayList;

public class FunctionSymbol extends Symbol
{
    private SimpleNode functionAST;
    private ArrayList<VarSymbol> arguments;
    private VarSymbol returnValue;

    public FunctionSymbol(SimpleNode functionAST, String id, ArrayList<VarSymbol> arguments, VarSymbol returnValue)
    {
        super(id);
        this.functionAST = functionAST;
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

    public ArrayList<VarSymbol> getArguments()
    {
        return arguments;
    }

    public void setArguments(ArrayList<VarSymbol> arguments)
    {
        this.arguments = arguments;
    }

    public VarSymbol getReturnValue()
    {
        return returnValue;
    }

    public void setReturnValue(VarSymbol returnValue)
    {
        this.returnValue = returnValue;
    }
}
