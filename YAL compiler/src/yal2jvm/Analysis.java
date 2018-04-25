package yal2jvm;

import yal2jvm.SemanticAnalysis.IfAnalysis;
import yal2jvm.SemanticAnalysis.ModuleAnalysis;
import yal2jvm.SemanticAnalysis.WhileAnalysis;
import yal2jvm.SymbolTables.FunctionSymbol;
import yal2jvm.SymbolTables.ImmediateSymbol;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
        if(mySymbols != null)
            unifiedSymbolTable.putAll(mySymbols);
        if(inheritedSymbols != null)
            unifiedSymbolTable.putAll(inheritedSymbols);

        return unifiedSymbolTable;
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

        if(inheritedSymbols != null)
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
        Node firstChild = rhsTree.jjtGetChild(0);
        if(firstChild.toString().equals("ARRAYSIZE")) {
            VarSymbol retVal = parseArraySize((ASTARRAYSIZE) firstChild);
            retVal = retVal.getCopy();
            retVal.setType("ARRAY");
            return retVal;
        }

        VarSymbol symbol = null;
        String previousType = null;
        int numChildren = rhsTree.jjtGetNumChildren();
        for(int i= 0; i < numChildren; i++)
        {
            ASTTERM child = (ASTTERM) rhsTree.jjtGetChild(i);
            VarSymbol previousSymbol = symbol;
            symbol = parseTerm(child);
            if(symbol == null)
                return null;
            String symbolType = symbol.getType();
            if(previousType == null)
                previousType = symbolType;
            else if(!previousType.equals(symbolType))
            {
                System.out.println("Variables dont match! Variable " + previousSymbol.getId() + " has type " + previousSymbol.getType() +
                        " and " + symbol.getId() + " has type " + symbol.getType() + "."); //TODO linha
                return null;
            }
        }

        return symbol;
    }

    protected VarSymbol parseArraySize(ASTARRAYSIZE arraySizeTree) {
        if(arraySizeTree.integer != null) {
            return new ImmediateSymbol(arraySizeTree.integer);
        }
        else {
            ASTSCALARACCESS child = (ASTSCALARACCESS) arraySizeTree.jjtGetChild(0);
            return parseScalarAccess(child);
        }
    }

    protected VarSymbol parseTerm(ASTTERM termTree)
    {
        if(termTree.integer != null)
            return new ImmediateSymbol(termTree.integer);

        Node child = termTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "CALL":
                return parseCall((ASTCALL) child);

            case "ARRAYACCESS":
                return parseArrayAccess((ASTARRAYACCESS) child);

            case "SCALARACCESS":
                return parseScalarAccess((ASTSCALARACCESS) child);

        }
        return null;
    }

    protected VarSymbol parseCall(ASTCALL callTree)
    {
        String module = callTree.module;
        if(module != null && !module.equals(ModuleAnalysis.moduleName))
            return new VarSymbol("", "UNDEFINED", true);

        String method = callTree.method;
        FunctionSymbol functionSymbol = (FunctionSymbol) functionNameToFunctionSymbol.get(method);
        if(functionSymbol == null)
        {
            System.out.println("Method " + method + " can´t be found."); //TODO linha
            return null;
        }

        ArrayList<VarSymbol> functionArguments = functionSymbol.getArguments();
        VarSymbol returnSymbol = null;

        if(callTree.jjtGetNumChildren() == 0) {
            if(callTree.jjtGetNumChildren() != functionArguments.size()){
                System.out.println("Method " + method + " arguments number(0) does not match expected" +
                        "number(" + functionArguments.size() + ") of arguments"); //TODO linha
                return null;
            }
        }
        else {
            ASTARGUMENTS astArgumentsList = (ASTARGUMENTS) callTree.jjtGetChild(0);
            ArrayList<String> argumentsTypes = parseArgumentList(astArgumentsList);
            if (argumentsTypes == null)
                return null;

            if(functionArguments.size() != argumentsTypes.size())
            {
                System.out.println("Method " + method + " arguments number(" + argumentsTypes.size() +
                        ") does not match expected number(" + functionArguments.size() + ") of arguments"); //TODO linha
                return null;
            }

            returnSymbol = functionSymbol.getReturnValue();
            for(int i = 0; i < functionArguments.size(); i++)
            {
                String argumentType = argumentsTypes.get(i);
                String exepectedArgumentType = functionArguments.get(i).getType();
                if(argumentType.equals(exepectedArgumentType) == false)
                {
                    System.out.println("Type " + argumentType + " of argument " + i + " of method " + method +
                            " call does not match expected type " + exepectedArgumentType + "."); //TODO linha
                    returnSymbol = null;
                }
            }
        }

        returnSymbol.setSize(Integer.MAX_VALUE);

        return returnSymbol;
    }

    protected ArrayList<String> parseArgumentList(ASTARGUMENTS argumentsListTree)
    {
        Integer childrenLength = argumentsListTree.jjtGetNumChildren();
        ArrayList<String> argumentsTypes = new ArrayList<String>();
        boolean haveFailed = false;
        for(int i = 0; i < childrenLength; i++)
        {
            ASTARGUMENT astargument = ((ASTARGUMENT) argumentsListTree.jjtGetChild(i));
            String idArg = astargument.idArg;
            Integer intArg = astargument.intArg;
            String stringArg = astargument.stringArg;

            if(idArg == null && intArg == null && stringArg == null)
            {
                System.out.println("Argument " + i + " is neither a variable, a string or an integer."); //TODO linha
                return null;
            }

            if(idArg != null)
            {
                VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(idArg);
                if(varSymbol == null)
                {
                    haveFailed = true;
                    continue;
                }
                argumentsTypes.add(varSymbol.getType().toString());
                continue;
            }
            else if(intArg != null)
                argumentsTypes.add("INTEGER");
            else
                argumentsTypes.add("STRING");
        }

        if(haveFailed)
            return null;

        return argumentsTypes;
    }

    protected VarSymbol parseArrayAccess(ASTARRAYACCESS arrayAccessTree)
    {
        String arrayId = arrayAccessTree.arrayID;

        VarSymbol arraySymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(arrayId);
        if(arraySymbol == null)
            return null;

        if(!arraySymbol.getType().equals("ARRAY"))
        {
            System.out.println("Access to index of variable +" + arrayId + " that is not an array."); //TODO linha
            return null;
        }

        ASTINDEX astindex = (ASTINDEX) arrayAccessTree.jjtGetChild(0);
        String indexSymbolId = astindex.indexID;
        if(indexSymbolId != null)
        {
            VarSymbol indexSymbol = (VarSymbol)checkSymbolExistsAndIsInitialized(indexSymbolId);
            if(indexSymbol == null)
                return null;
        }
        else
        {
            Integer indexValue = astindex.indexValue;
            if(indexValue >= arraySymbol.getSize())
            {
                System.out.println("Access to out of bounds " + indexValue + " in array " + arrayId +"."); //TODO linha
                return null;
            }
        }

        arraySymbol = arraySymbol.getCopy();
        arraySymbol.setType("INTEGER");

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
            System.out.println("Access to undeclared variable " + id + "."); //TODO linha
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
            System.out.println("Access to undeclared variable " + symbolId + "."); //TODO linha
            return null;
        }
        if(!indexSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable " + symbolId + "."); //TODO linha
            return null;
        }

        return indexSymbol;
    }


    protected VarSymbol parseScalarAccess(ASTSCALARACCESS scalarAccessTree)
    {
        String id = scalarAccessTree.id;
        boolean sizeAccess = false;
        if(id.contains("."))
        {
            int dotIdx = id.indexOf(".");
            if(id.substring(dotIdx + 1).equals("size"))
                sizeAccess = true;
            id = id.substring(0, dotIdx);
        }
        System.out.println("id: " + id); //TODO
        VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(id);
        if(varSymbol == null)
            return null;

        if (varSymbol.getType().equals("ARRAYELEMENT") && !sizeAccess) {
            System.out.println("Access to size of variable " + id + " that is not an array."); //TODO linha
            return null;
        }

        if(sizeAccess)
        {
            varSymbol = varSymbol.getCopy();
            varSymbol.setType("INTEGER");
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
            System.out.println("Access to undeclared variable " + id + "."); //TODO linha
            return null;
        }

        //TODO nao faz sentido em todos os casos apenas quando é read da variavel
        if(!varSymbol.isInitialized())
        {
            System.out.println("Access to uninitialized variable " + id + "."); //TODO linha
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

            VarSymbol varSymbol = new VarSymbol(astscalarelement.id, "INTEGER", initialized);

            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                int arraySize;
                if(astarraysize.integer != null)
                    arraySize = astarraysize.integer;
                else
                {
                   ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                   VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);
                   arraySize = scalarAccessSymbol.getSize();
                }
                varSymbol.setSize(arraySize);
                varSymbol.setType("ARRAY");
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

            boolean initialized;
            int size;
            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a[]=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                int arraySize;
                if(astarraysize.integer != null)
                    arraySize = astarraysize.integer;
                else
                {
                    ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                    VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);
                    arraySize = scalarAccessSymbol.getSize();
                }
                initialized = true;
                size = arraySize;
            }
            else
            {
                if(declarationTree.integer != null) //if is from type a[]=CONST and a[] have not been previously defined, it cannot happen
                {
                    if (symbol != null) //if is from type a[]=CONST and a[] have been previously defined
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

                initialized = false; //if frm type a[]; variable not initialized and size = -1
                size = -1;
            }

            VarSymbol varSymbol = new VarSymbol(astarrayelement.id, "ARRAY", initialized, size);

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

    protected boolean parseAssign(ASTASSIGN assignTree)
    {
        VarSymbol rhsSymbol = null;
        SimpleNode rhsTree = (SimpleNode) assignTree.jjtGetChild(1);
        if(rhsTree != null)
            rhsSymbol = parseRhs(rhsTree);
        if(rhsSymbol == null)
            return false;

        //TODO DEBUG TIRAR
        System.out.println("From parseAssign, rhsSymbol");
        System.out.println("symbol id: " + rhsSymbol.getId());
        System.out.println("symbol type: " + rhsSymbol.getType());
        System.out.println("symbol size: " + rhsSymbol.getSize());



        SimpleNode lhsTree = (SimpleNode) assignTree.jjtGetChild(0);
        VarSymbol lhsSymbol = getLhsVariable(lhsTree);
        if(lhsSymbol == null)
            return false;
        //TODO
       /* if(!lhsSymbol.isInitialized())
        {
            if(rhsSymbol.getType().equals("UNDEFINED"))
                lhsSymbol.setType("INTEGER");
            else
                lhsSymbol.setType(rhsSymbol.getType());
            lhsSymbol.setSize(rhsSymbol.getSize());
        }*/

        if(lhsSymbol.getType().equals("UNDEFINED"))
        {
            if(rhsSymbol.getType().equals("UNDEFINED"))
                lhsSymbol.setType("INTEGER");
            else
                lhsSymbol.setType(rhsSymbol.getType());
            lhsSymbol.setSize(rhsSymbol.getSize());
        }
        String lhsSymbolType = lhsSymbol.getType();
        String rhsSymbolType = rhsSymbol.getType();


        if(! (lhsSymbolType.equals("ARRAY") && rhsSymbol.getType().equals("INTEGER"))) //for A=5; in which A is an array and all its elements are set to 5
            if(!rhsSymbolType.equals("UNDEFINED")) //for A=m.f(); in which m.f() function is from another module that we not know the return value, so it can be INTEGER or ARRAY
                if(!lhsSymbolType.equals(rhsSymbolType))
                {
                    System.out.println("Variables dont match! Variable " + lhsSymbol.getId() + " has type " + lhsSymbolType +
                            " and " + rhsSymbol.getId() + " has type " + rhsSymbolType + "."); //TODO linha
                    return false;
                }
        lhsSymbol.setInitialized(true);

        //TODO DEBUG TIRAR
        System.out.println("From parseAssign, lhsSymbol");
        System.out.println("symbol id: " + lhsSymbol.getId());
        System.out.println("symbol type: " + lhsSymbol.getType());
        System.out.println("symbol size: " + lhsSymbol.getSize());

        if((inheritedSymbols.get(lhsSymbol.getId()) == null) && (mySymbols.get(lhsSymbol.getId()) == null))
            mySymbols.put(lhsSymbol.getId(), lhsSymbol);

        return true;
    }

    private VarSymbol getLhsVariable(SimpleNode lhsTree)
    {
        VarSymbol symbol = null;
        String id;
        Node child = lhsTree.jjtGetChild(0);
        switch(child.toString())
        {
            case "ARRAYACCESS":
                id = ((ASTARRAYACCESS) child).arrayID;
                symbol = (VarSymbol) checkSymbolExistsAndIsInitialized(id);
                if(symbol == null)
                    return null;
                ASTINDEX astindex = (ASTINDEX) child.jjtGetChild(0);
                if(!parseIndex(astindex, symbol))
                    return null;
                symbol = symbol.getCopy(); //symbol type will be altered but only for this case, so we need a copy
                symbol.setType("INTEGER");
                break;
            case "SCALARACCESS":
                id = ((ASTSCALARACCESS) child).id;
                symbol = (VarSymbol) hasAccessToSymbol(id);

                if(symbol == null) {
                    symbol = new VarSymbol(id, "UNDEFINED", false);
                }

                break;
        }

        return symbol;
    }

    protected boolean parseIndex(ASTINDEX astIndex, VarSymbol arraySymbol)
    {
        String indexSymbolId = astIndex.indexID;
        if (indexSymbolId != null)
        {
            VarSymbol indexSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(indexSymbolId);
            if (indexSymbol == null)
                return false;
        }
        else
        {
            Integer indexValue = astIndex.indexValue;
            if (indexValue >= arraySymbol.getSize())
            {
                System.out.println("Access to out of bounds " + indexValue + " in array " + arraySymbol.getId() + "."); //TODO linha
                return false;
            }

        }

        return true;
    }

    protected boolean parseExprTest(ASTEXPRTEST astExprtest)
    {
        ASTLHS astLhs = (ASTLHS) astExprtest.jjtGetChild(0);
        VarSymbol lhsSymbol = parseLhs(astLhs);
        if(lhsSymbol == null)
            return false;

        ASTRHS astRhs = (ASTRHS) astExprtest.jjtGetChild(1);
        VarSymbol rhsSymbol = parseLhs(astRhs);
        if(rhsSymbol == null)
            return false;

        if(!lhsSymbol.getType().equals(rhsSymbol.getType()))
        {
            System.out.println("Variables must have same type to be compared. Variable " + lhsSymbol.getId() + " has type "
                    + lhsSymbol.getType() + " and variable " + rhsSymbol.getId() + " has type " + rhsSymbol.getType() + "."); //TODO linha
            return false;
        }

        if(!lhsSymbol.getType().equals("ARRAY"))
        {
            System.out.println("Variables must be INTEGER to be compared. Variable " + lhsSymbol.getId() + " has type "
                    + lhsSymbol.getType() + " and variable " + rhsSymbol.getId() + " has type " + rhsSymbol.getType() + "."); //TODO linha
            return false;
        }

        return true;
    }

    protected void parseStmtLst(ASTSTATEMENTS astStatements)
    {
        int statementsNumChilds = astStatements.jjtGetNumChildren();
        for(int i = 0; i < statementsNumChilds; i++)
        {
            SimpleNode node = (SimpleNode) astStatements.jjtGetChild(i);
            String nodeId = node.toString();
            switch(nodeId)
            {
                case "WHILE":
                    WhileAnalysis whileAnalysis = new WhileAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    whileAnalysis.parse();
                    mySymbols.putAll(whileAnalysis.mySymbols);
                    break;
                case "IF":
                    IfAnalysis ifAnalysis = new IfAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    ifAnalysis.parse();
                    mySymbols.putAll(ifAnalysis.mySymbols);
                    break;
                case "CALL":
                    parseCall((ASTCALL) node);
                    break;
                case "ASSIGN":
                    parseAssign((ASTASSIGN) node);
                    break;
            }
        }
    }

    protected HashMap<String,Symbol> setAllSymbolsAsNotInitialized(HashMap<String, Symbol> symbols)
    {
        HashMap<String, Symbol> symbolsNotInitialized = new HashMap<String, Symbol>();

        Iterator it = symbols.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            String symbolName = (String) pair.getKey();
            VarSymbol symbol = (VarSymbol) pair.getValue();
            symbol.setInitialized(false);
            symbolsNotInitialized.put(symbolName, symbol);
            symbol.setSize(-1);
        }

        return symbolsNotInitialized;
    }

}
