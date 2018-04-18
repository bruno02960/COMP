package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{
    private HashMap<String, Symbol> functionNameToFunctionSymbolOfModule;

    public FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                            HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
       super(ast, inheritedSymbols);
       this.functionNameToFunctionSymbolOfModule = functionNameToFunctionSymbolOfModule;
    }

    @Override
    protected void parse()
    {
        parseScalarAccess();
        parseArrayAccess();
    }

    private VarSymbol parseLhs(SimpleNode lhs) {
        switch(lhs.jjtGetChild(0).toString()) {
            case "ARRAYACCESS":
                return parseArrayAccess();
                break;
            case "SCALARACCESS":
                return parseScalarAccess();
                break;
        }

        return null;
    }

    private VarSymbol parseWhile() {
        ASTEXPRTEST exprtest = ((ASTEXPRTEST) ast.jjtGetChild(0));

        SimpleNode lhs = (SimpleNode) exprtest.jjtGetChild(0);
        return parseLhs(lhs);
    }

    private VarSymbol parseArrayAccess()
    {
        String array = ((ASTARRAYACCESS) ast).arrayID;

        int lbrIdx = array.indexOf("[");
        int rbrIdx = array.indexOf("]");

        String id = array.substring(0, lbrIdx);
        String idxStr = array.substring(lbrIdx + 1, rbrIdx);

        int index = Integer.parseInt(idxStr);
        System.out.println("\nindex: " + index);//TODO

        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(id);
        if(varSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        if(!varSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }
        else {
            if(index >= varSymbol.getSize()) {
                System.out.println("Access to out of bounds " + index + " in array " + id +"."); //TODO linha
                return null;
            }
        }

        if(!varSymbol.getType().equals("ARRAYELEMENT"))
        {
            System.out.println("Access to index of variable +" + id + " that is not an array."); //TODO linha
            return null;
        }

        return varSymbol;
    }


    private VarSymbol parseScalarAccess()
    {
        String id = ((ASTSCALARACCESS) ast.id;
        boolean sizeAccess = false;
        if(id.contains("."))
        {
            int dotIdx = id.indexOf(".");
            if(id.substring(dotIdx + 1).equals("size"))
                sizeAccess = true;
            id = id.substring(0, dotIdx);
        }
        System.out.println("\nid: " + id);//TODO
        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(id);
        if(varSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        if(varSymbol.isInitialized() == null)
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        if(varSymbol.getType().equals("ARRAYELEMENT") && !sizeAccess)
        {
            System.out.println("Access to size of variable +" + id + " that is not an array."); //TODO linha
            return null;
        }

        return varSymbol;
    }

    //don't use in Declaration
    private VarSymbol parseScalarElement()
    {
        String id = ((ASTSCALARACCESS) ast.jjtGetChild(0)).id;
        System.out.println("\nid: " + id);//TODO
        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(id);
        if(varSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        //TODO nao faz sentido em todos os casos apenas quando é read da variavel
        if(varSymbol.getValues() == null)
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        return varSymbol;
    }
}
