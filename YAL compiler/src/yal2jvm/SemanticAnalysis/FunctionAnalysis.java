package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.ASTSCALARACCESS;
import yal2jvm.ast.SimpleNode;

import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{

    public FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
       super(ast, inheritedSymbols);
    }

    @Override
    protected void parse()
    {
        parseScalarAccess();
    }

    private Symbol parseScalarAccess()
    {
        String id = ((ASTSCALARACCESS) ast.jjtGetChild(0)).id;
        boolean sizeAccess = false;
        if(id.contains("."))
        {
            int dotIdx = id.indexOf(".");
            if(id.substring(dotIdx + 1).equals("size"))
                sizeAccess = true;
            id = id.substring(0, dotIdx);
        }
        System.out.println("\nid: " + id);//TODO
        Symbol symbol = hasAccessToSymbol(id);
        if(symbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        if(symbol.getValues() == null)
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        if(symbol.getType().equals("ARRAYELEMENT") && !sizeAccess)
        {
            System.out.println("Access to size of variable +" + id + " that is not an array."); //TODO linha
            return null;
        }

        return symbol;
    }

    //don't use in Declaration
    private Symbol parseScalarElement()
    {
        String id = ((ASTSCALARACCESS) ast.jjtGetChild(0)).id;
        System.out.println("\nid: " + id);//TODO
        Symbol symbol = hasAccessToSymbol(id);
        if(symbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        //TODO nao faz sentido em todos os casos apenas quando é read da variavel
        if(symbol.getValues() == null)
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        return symbol;
    }
}
