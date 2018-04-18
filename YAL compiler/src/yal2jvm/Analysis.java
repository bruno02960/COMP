package yal2jvm;

import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Analysis
{
    protected HashMap<String, Symbol> mySymbols;
    protected HashMap<String, Symbol> inheritedSymbols;
    protected SimpleNode ast;

    protected Analysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
        this.ast = ast;
        this.inheritedSymbols = inheritedSymbols;
        this.mySymbols = new HashMap<String, Symbol>();
    }

    protected abstract void parse();

    protected HashMap<String, Symbol> getUnifiedSymbolTable()
    {
        HashMap<String, Symbol> unifiedSymbolTable = new HashMap<String, Symbol>();
        unifiedSymbolTable.putAll(mySymbols);
        unifiedSymbolTable.putAll(inheritedSymbols);

        return  unifiedSymbolTable;
    }

    protected Symbol hasAccessToSymbol(String symbolId)
    {
        Symbol symbol;
        symbol = mySymbols.get(symbolId);
        if(symbol != null)
            return symbol;
        else
            symbol = inheritedSymbols.get(symbolId);

        return symbol;
    }

    protected VarSymbol parseLhs(SimpleNode lhsTree)
    {
        Node child = lhsTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARRAYACCESS":
                return parseArrayAccess((ASTARRAYACCESS) child);
            case "SCALARACCESS":
                return parseScalarAccess((ASTSCALARACCESS) child);
        }

        return null;
    }

    protected VarSymbol parseRhs(SimpleNode rhsTree)
    {
        Node child = rhsTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARRAYSIZE":

            case "TERM":
                return parseTerm((ASTTERM) child);
        }

        return null;
    }

    protected VarSymbol parseTerm(ASTTERM termTree)
    {
        Node child = termTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "CALL":
                return parseCall((ASTCALL) child);

            case "ARRAYACCESS":

            case "SCALARACCESS":

        }
        return null;
    }

    protected VarSymbol parseCall(ASTCALL callTree)
    {
        Node child = callTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARGUMENTLIST":
                return parseArgumentList((ASTARGUMENTS) child);
        }
        return null;
    }

    protected VarSymbol parseArgumentList(ASTARGUMENTS argumentListTree)
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

    protected VarSymbol parseArrayAccess(ASTARRAYACCESS arrayAccessTree)
    {
        String array = ((ASTARRAYACCESS) arrayAccessTree.jjtGetChild(0)).arrayID;

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


    protected VarSymbol parseScalarAccess(ASTSCALARACCESS scalarAccessTree)
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

        if(varSymbol.getType().equals("ARRAYELEMENT") && !sizeAccess)
        {
            System.out.println("Access to size of variable +" + id + " that is not an array."); //TODO linha
            return null;
        }

        return varSymbol;
    }

    //don't use in Declaration
    protected VarSymbol parseScalarElement(ASTSCALARELEMENT ast)
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
        if(!varSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }

        return varSymbol;
    }

    protected VarSymbol parseDeclaration(ASTDECLARATION declarationTree)
    {
        Node child = declarationTree.jjtGetChild(0);
        if(child instanceof ASTSCALARELEMENT)
        {
            ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
            Symbol symbol = hasAccessToSymbol(astscalarelement.id);
            if(symbol != null)
            {
                System.out.println("Variable " + astscalarelement.id + " already declared."); //TODO linha
                return null;
            }

            //parse right hand side if existent
            if(declarationTree.integer != null) //if is from type a=CONST;
            {

            }
            child = declarationTree.jjtGetChild(0);

           /* values = getValuesFromScalarElementDeclarationIfExists(astscalarelement);
            name = astscalarelement.id;
            varSymbol = new VarSymbol(name, type, true);*/
        }
        else if(child instanceof ASTARRAYELEMENT)
        {
            ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
           /* boolean isInitialized = IsAssignRHSFromArrayElementInitialized(child);
            name = astarrayelement.id;
            int size =
                    varSymbol = new VarSymbol(name, type, isInitialized, );*/

        }


       /* Node node = child.jjtGetChild(0);

        //TODO DEBUG TIRAR
        System.out.println("symbol name: " + name);
        System.out.println("symbol type: " + type);


        mySymbols.put(varSymbol.getId(), varSymbol);*/
        return null;
    }
}
