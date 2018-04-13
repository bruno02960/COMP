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
}
