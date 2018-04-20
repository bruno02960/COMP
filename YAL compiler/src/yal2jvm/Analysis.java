package yal2jvm;

import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Analysis
{
    protected HashMap<String, Symbol> mySymbols;
    protected HashMap<String, Symbol> inheritedSymbols;
    protected HashMap<String, Symbol> functionNameToFunctionSymbol;
    protected SimpleNode ast;

    protected Analysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                       HashMap<String, Symbol> functionNameToFunctionSymbol)
    {
        this.ast = ast;
        this.inheritedSymbols = inheritedSymbols;
        this.mySymbols = new HashMap<String, Symbol>();
        this.functionNameToFunctionSymbol = functionNameToFunctionSymbol;
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
        Symbol symbol = null;
        if(mySymbols != null)
        {
            symbol = mySymbols.get(symbolId);
            if(symbol != null)
                return symbol;
        }
        else if(inheritedSymbols != null)
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
        String arrayId = arrayAccessTree.arrayID;

        VarSymbol arraySymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(arrayId);
        if(arraySymbol == null)
            return null;

        if(!arraySymbol.getType().equals("ARRAYELEMENT"))
        {
            System.out.println("Access to index of variable +" + arrayId + " that is not an array."); //TODO linha
            return null;
        }

        ASTINDEX astindex = (ASTINDEX) arrayAccessTree.jjtGetChild(0);
        String indexSymbolId = astindex.indexID;
        VarSymbol indexSymbol;
        if(indexSymbolId != null)
        {
            indexSymbol = (VarSymbol)checkSymbolExistsAndIsInitialized(indexSymbolId);
            if(indexSymbol != null)
            {
                if(astindex.indexValue >= indexSymbol.getSize())
                {
                    System.out.println("Access to out of bounds " + astindex.indexValue + " in array " + indexSymbolId +"."); //TODO linha
                    return null;
                }
            }
        }

        return arraySymbol;




       /* int lbrIdx = array.indexOf("[");
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

*/
    }

    private Symbol checkSymbolExistsAndIsInitialized(String symbolId)
    {
        VarSymbol indexSymbol = (VarSymbol) hasAccessToSymbol(symbolId);
        if(indexSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + symbolId + "."); //TODO linha
            return null;
        }
        if(!indexSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable +" + symbolId + "."); //TODO linha
            return null;
        }

        return indexSymbol;
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
        VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(id);
        return varSymbol;


       /* if(varSymbol == null)
        {
            System.out.println("Access to undeclared variable +" + id + "."); //TODO linha
            return null;
        }

        //TODO nao faz sentido em todos os casos apenas quando é read da variavel
        if(!varSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable +" + id + "."); //TODO linha
            return null;
        }*/
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
            boolean initialized = false;
            if(declarationTree.integer != null) //if is from type a=CONST;
                initialized = true;

            VarSymbol varSymbol = new VarSymbol(astscalarelement.id, "SCALARELEMENT", initialized);

            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                varSymbol.setType("ARRAYELEMENT");
                varSymbol.setSize(astarraysize.integer);
                varSymbol.setInitialized(true);
            }

            //TODO DEBUG TIRAR
            System.out.println("From parseDeclaration, ASTSCALARELEMENT");
            System.out.println("symbol id: " + varSymbol.getId());
            System.out.println("symbol type: " + varSymbol.getType());
            System.out.println("symbol size: " + varSymbol.getSize());


            mySymbols.put(varSymbol.getId(), varSymbol);
            return varSymbol;
        }
        else if(child instanceof ASTARRAYELEMENT)
        {
            ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
            Symbol symbol = hasAccessToSymbol(astarrayelement.id);
            if(symbol != null && declarationTree.integer == null) //if it has already been declared and its not just a initialization
            {
                System.out.println("Variable " + astarrayelement.id + " already declared."); //TODO linha
                return null;
            }

            boolean initialized = false;
            int size = -1;
            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a[]=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                initialized = true;
                size = astarraysize.integer;
            }
            else
            {
                if(declarationTree.integer != null) //if is from type a[]=CONST and a[] have not been previously defined, it cannot happen
                {
                    if(symbol != null) //if is from type a[]=CONST and a[] have been previously defined
                    {
                        VarSymbol varSymbol = (VarSymbol) symbol;
                        varSymbol.setInitialized(true);
                        return varSymbol;
                    }
                    else
                    {
                        System.out.println("Variable " + astarrayelement.id + " has the size not defined. Error assigning " +
                                declarationTree.integer + " to all elements of " + astarrayelement.id); //TODO linha
                        return null;
                    }
                }
            }

            VarSymbol varSymbol = new VarSymbol(astarrayelement.id, "ARRAYELEMENT", initialized, size);

            //TODO DEBUG TIRAR
            System.out.println("From parseDeclaration, ASTARRAYELEMENT");
            System.out.println("symbol id: " + varSymbol.getId());
            System.out.println("symbol type: " + varSymbol.getType());
            System.out.println("symbol size: " + varSymbol.getSize());


            mySymbols.put(varSymbol.getId(), varSymbol);
            return varSymbol;
        }

        return null;
    }

    protected VarSymbol parseAssign(ASTASSIGN assignTree)
    {
        VarSymbol lhsSymbol = null;
        SimpleNode lhsTree = (SimpleNode) ast.jjtGetChild(0);
        if(lhsTree != null)
        {
            lhsSymbol = parseLhs(lhsTree);
            if(lhsSymbol == null)
                return null;
        }

        SimpleNode rhsTree = (SimpleNode) ast.jjtGetChild(1);
        if(rhsTree != null)
        {
            VarSymbol rhsSymbol = parseRhs(rhsTree);
            if(rhsSymbol == null)
                return null;
        }

        lhsSymbol.setInitialized(true);
        return lhsSymbol;
    }
}
