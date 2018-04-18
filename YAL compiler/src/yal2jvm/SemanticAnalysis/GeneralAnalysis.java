package yal2jvm.SemanticAnalysis;

import com.sun.xml.internal.bind.v2.model.core.ID;
import sun.java2d.pipe.SpanShapeRenderer;
import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralAnalysis
{
    public static Symbol hasAccessToSymbol(HashMap<String,Symbol> mySymbols,
                                           HashMap<String,Symbol> inheritedSymbols, String symbolId)
    {
        Symbol symbol;
        symbol = mySymbols.get(symbolId);
        if(symbol != null)
            return symbol;
        else
            symbol = inheritedSymbols.get(symbolId);

        return symbol;
    }

    public static VarSymbol parseLhs(HashMap<String,Symbol> mySymbols,
                                     HashMap<String,Symbol> inheritedSymbols, SimpleNode lhsTree)
    {
        Node child = lhsTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARRAYACCESS":
                return parseArrayAccess(mySymbols, inheritedSymbols, (SimpleNode) child);
            case "SCALARACCESS":
                return parseScalarAccess(mySymbols, inheritedSymbols, (SimpleNode) child);
        }

        return null;
    }

    public static VarSymbol parseRhs(HashMap<String,Symbol> mySymbols,
                                     HashMap<String,Symbol> inheritedSymbols, SimpleNode rhsTree)
    {
        Node child = rhsTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARRAYSIZE":

            case "TERM":
                return parseTerm(mySymbols, inheritedSymbols, (SimpleNode) child);
        }

        return null;
    }

    public static VarSymbol parseTerm(HashMap<String,Symbol> mySymbols,
                                      HashMap<String,Symbol> inheritedSymbols, SimpleNode termTree)
    {
        Node child = termTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "CALL":
                return parseCall(mySymbols, inheritedSymbols, (SimpleNode) child);

            case "ARRAYACCESS":

            case "SCALARACCESS":

        }
        return null;
    }

    public static VarSymbol parseCall(HashMap<String,Symbol> mySymbols,
                                      HashMap<String,Symbol> inheritedSymbols, SimpleNode callTree)
    {
        Node child = callTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARGUMENTLIST":
                return parseArgumentList(mySymbols, inheritedSymbols, (SimpleNode) child);
        }
        return null;
    }

    public static VarSymbol parseArgumentList(HashMap<String,Symbol> mySymbols,
                                      HashMap<String,Symbol> inheritedSymbols, SimpleNode argumentListTree)
    {
        Integer childrenLength = argumentListTree.jjtGetNumChildren();
        ArrayList<VarSymbol> symbolArray = new ArrayList<VarSymbol>();
        for(int i = 0; i < childrenLength; i++)
        {
            Node child = argumentListTree.jjtGetChild(i);
            ASTARGUMENT astargument = ((ASTARGUMENT) child);
            String idArg = astargument.idArg;
            Integer intArg = astargument.intArg;
            String stringArg = astargument.stringArg;

            if(idArg == null && intArg == null && stringArg == null)
            {
                System.out.println("ArgumentList Child " + i + " has all attributes set to null");
                return null;
            }

            //if(idArg != null)
                //symbolArray.add(idArg); //TODO: fazer isto de acordo com o valor esperado e retornar boolean
        }
        return null;
    }

    public static VarSymbol parseArrayAccess(HashMap<String,Symbol> mySymbols,
                                             HashMap<String,Symbol> inheritedSymbols, SimpleNode arrayAccessTree)
    {
        String array = ((ASTARRAYACCESS) arrayAccessTree.jjtGetChild(0)).arrayID;

        int lbrIdx = array.indexOf("[");
        int rbrIdx = array.indexOf("]");

        String id = array.substring(0, lbrIdx);
        String idxStr = array.substring(lbrIdx + 1, rbrIdx);

        int index = Integer.parseInt(idxStr);
        System.out.println("\nindex: " + index);//TODO

        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(mySymbols, inheritedSymbols, id);
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


    public static VarSymbol parseScalarAccess(HashMap<String,Symbol> mySymbols,
                                              HashMap<String,Symbol> inheritedSymbols, SimpleNode scalarAccessTree)
    {
        String id = ((ASTSCALARACCESS) scalarAccessTree.jjtGetChild(0)).id;
        boolean sizeAccess = false;
        if(id.contains("."))
        {
            int dotIdx = id.indexOf(".");
            if(id.substring(dotIdx + 1).equals("size"))
                sizeAccess = true;
            id = id.substring(0, dotIdx);
        }
        System.out.println("\nid: " + id);//TODO
        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(mySymbols, inheritedSymbols, id);
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

        if(varSymbol.getType().equals("ARRAYELEMENT") && !sizeAccess)
        {
            System.out.println("Access to size of variable +" + id + " that is not an array."); //TODO linha
            return null;
        }

        return varSymbol;
    }

    //don't use in Declaration
    public static VarSymbol parseScalarElement(HashMap<String,Symbol> mySymbols,
                                               HashMap<String,Symbol> inheritedSymbols, SimpleNode ast)
    {
        String id = ((ASTSCALARACCESS) ast.jjtGetChild(0)).id;
        System.out.println("\nid: " + id);//TODO
        VarSymbol varSymbol = (VarSymbol) hasAccessToSymbol(mySymbols,inheritedSymbols, id);
        if(varSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        //TODO nao faz sentido em todos os casos apenas quando é read da variavel
        if(!varSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        return varSymbol;
    }
}
