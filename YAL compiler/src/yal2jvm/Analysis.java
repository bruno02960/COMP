package yal2jvm;

import yal2jvm.SemanticAnalysis.IfAnalysis;
import yal2jvm.SemanticAnalysis.ModuleAnalysis;
import yal2jvm.SemanticAnalysis.WhileAnalysis;
import yal2jvm.SymbolTables.*;
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
        this.mySymbols = new HashMap<>();
        this.functionNameToFunctionSymbol = functionNameToFunctionSymbol;
    }

    protected abstract void parse();

    protected HashMap<String, Symbol> getUnifiedSymbolTable()
    {
        HashMap<String, Symbol> unifiedSymbolTable = new HashMap<>();
        if(mySymbols != null)
            unifiedSymbolTable.putAll(mySymbols);
        if(inheritedSymbols != null)
            unifiedSymbolTable.putAll(inheritedSymbols);

        return unifiedSymbolTable;
    }

    private Symbol hasAccessToSymbol(String symbolId)
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

    private VarSymbol parseLhs(SimpleNode lhsTree)
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

    private VarSymbol parseRhs(SimpleNode rhsTree)
    {
        Node firstChild = rhsTree.jjtGetChild(0);
        if(firstChild.toString().equals("ARRAYSIZE"))
        {
            VarSymbol retVal = parseArraySize((ASTARRAYSIZE) firstChild);
            if(retVal == null)
                return null;
            retVal = retVal.getCopy();
            retVal.setType("ARRAYSIZE");
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
                System.out.println("Line " + child.getBeginLine() + ": Variables dont match! Variable "
                        + previousSymbol.getId() + " has type " + previousSymbol.getType() +
                        " and " + symbol.getId() + " has type " + symbol.getType() + ".");
                return null;
            }
        }

        return symbol;
    }

    private VarSymbol parseArraySize(ASTARRAYSIZE arraySizeTree) {
        if(arraySizeTree.integer != null) {
            return new ImmediateSymbol("[" + arraySizeTree.integer + "]");
        }
        else {
            ASTSCALARACCESS child = (ASTSCALARACCESS) arraySizeTree.jjtGetChild(0);
            return parseScalarAccess(child);
        }
    }

    private VarSymbol parseTerm(ASTTERM termTree)
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

    private VarSymbol parseCall(ASTCALL callTree)
    {
        String module = callTree.module;
        if(module != null && !module.equals(ModuleAnalysis.moduleName))
        {
            if(callTree.jjtGetNumChildren() > 0)
            {
                ASTARGUMENTS astarguments = (ASTARGUMENTS) callTree.jjtGetChild(0);
                parseArgumentList(astarguments);
            }
            return new VarSymbol("", SymbolType.UNDEFINED.toString(), true);
        }

        String method = callTree.method;
        FunctionSymbol functionSymbol = (FunctionSymbol) functionNameToFunctionSymbol.get(method);
        if(functionSymbol == null)
        {
            System.out.println("Line " + callTree.getBeginLine() + ": Method " + method + " can´t be found.");
            return null;
        }

        ArrayList<VarSymbol> functionArguments = functionSymbol.getArguments();
        VarSymbol returnSymbol = null;

        if(callTree.jjtGetNumChildren() == 0) {
            if(callTree.jjtGetNumChildren() != functionArguments.size()){
                System.out.println("Line " + callTree.getBeginLine() + ": Method " + method + " arguments number(0)" +
                        "does not match expected number(" + functionArguments.size() + ") of arguments");
                return null;
            }
        }
        else {
            ASTARGUMENTS astarguments = (ASTARGUMENTS) callTree.jjtGetChild(0);
            ArrayList<String> argumentsTypes = parseArgumentList(astarguments);
            if (argumentsTypes == null)
                return null;

            if(functionArguments.size() != argumentsTypes.size())
            {
                System.out.println("Line " + astarguments.getBeginLine() + ": Method " + method + " arguments" +
                        "number(" + argumentsTypes.size() + ") does not match expected number(" +
                        functionArguments.size() + ") of arguments");
                return null;
            }

            returnSymbol = functionSymbol.getReturnValue();
            for(int i = 0; i < functionArguments.size(); i++)
            {
                String argumentType = argumentsTypes.get(i);
                String expectedArgumentType = functionArguments.get(i).getType();
                if(!argumentType.equals(expectedArgumentType))
                {
                    System.out.println("Line " + astarguments.getBeginLine() + ": Type " + argumentType +
                            " of argument " + i+1 + " of method " + method +
                            " call does not match expected type " + expectedArgumentType + ".");
                    returnSymbol = null;
                }
            }
        }

        return returnSymbol;
    }

    private ArrayList<String> parseArgumentList(ASTARGUMENTS astarguments)
    {
        Integer childrenLength = astarguments.jjtGetNumChildren();
        ArrayList<String> argumentsTypes = new ArrayList<>();
        boolean haveFailed = false;
        for(int i = 0; i < childrenLength; i++)
        {
            ASTARGUMENT astargument = ((ASTARGUMENT) astarguments.jjtGetChild(i));
            String idArg = astargument.idArg;
            Integer intArg = astargument.intArg;
            String stringArg = astargument.stringArg;

            if(idArg == null && intArg == null && stringArg == null)
            {
                System.out.println("Line " + astargument.getBeginLine() + ": Argument " + i + " is neither a variable,"
                        + "a string or an integer.");
                return null;
            }

            if(idArg != null)
            {
                VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(astargument, idArg);
                if(varSymbol == null)
                {
                    haveFailed = true;
                    continue;
                }
                argumentsTypes.add(varSymbol.getType());
            }
            else if(intArg != null)
                argumentsTypes.add(SymbolType.INTEGER.toString());
            else
                argumentsTypes.add("STRING");
        }

        if(haveFailed)
            return null;

        return argumentsTypes;
    }

    private VarSymbol parseArrayAccess(ASTARRAYACCESS arrayAccessTree)
    {
        String arrayId = arrayAccessTree.arrayID;

        VarSymbol arraySymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(arrayAccessTree, arrayId);
        if(arraySymbol == null)
            return null;

        if(!arraySymbol.getType().equals(SymbolType.ARRAY.toString()))
        {
            System.out.println("Line " + arrayAccessTree.getBeginLine() + ": Access to index of variable +" + arrayId
                    + " that is not an array.");
            return null;
        }

        ASTINDEX astindex = (ASTINDEX) arrayAccessTree.jjtGetChild(0);
        String indexSymbolId = astindex.indexID;
        if(indexSymbolId != null)
        {
            VarSymbol indexSymbol = (VarSymbol)checkSymbolExistsAndIsInitialized(astindex, indexSymbolId);
            if(indexSymbol == null)
                return null;
        }

        arraySymbol = arraySymbol.getCopy();
        arraySymbol.setType(SymbolType.INTEGER.toString());

        return arraySymbol;
    }

    private Symbol checkSymbolExistsAndIsInitialized(SimpleNode ast, String symbolId)
    {
        VarSymbol indexSymbol = (VarSymbol) hasAccessToSymbol(symbolId);
        if(indexSymbol == null)
        {
            System.out.println("Line " + ast.getBeginLine() + ": Access to undeclared variable " + symbolId + ".");
            return null;
        }
        if(!indexSymbol.isInitialized())
        {
            System.out.println("Line " + ast.getBeginLine() + ": Access to uninitialized variable " + symbolId + ".");
            return null;
        }

        return indexSymbol;
    }


    private VarSymbol parseScalarAccess(ASTSCALARACCESS scalarAccessTree)
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

        VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(scalarAccessTree, id);
        if(varSymbol == null)
            return null;

        if (varSymbol.getType().equals("INTEGER") && sizeAccess)
        {
            System.out.println("Line " + scalarAccessTree.getBeginLine() + ": Access to size of variable " + id +
                    " that is not an array.");
            return null;
        }

        if(varSymbol.getType().equals("ARRAY") && varSymbol.isSizeSet() == false)
        {
            System.out.println("Line " + scalarAccessTree.getBeginLine() + ": Access to size of variable " + id +
                    " that has not size defined");
            return null;
        }

        if(sizeAccess)
        {
            varSymbol = varSymbol.getCopy();
            varSymbol.setType(SymbolType.INTEGER.toString());
        }

        return varSymbol;
    }

    protected VarSymbol parseDeclaration(ASTDECLARATION declarationTree)
    {
        Node child = declarationTree.jjtGetChild(0);
        if(child instanceof ASTSCALARELEMENT)
        {
            ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
            VarSymbol symbol = (VarSymbol) hasAccessToSymbol(astscalarelement.id);

            if(symbol != null)
            {
                if(!symbol.isInitialized() && declarationTree.integer != null) {
                    symbol.setInitialized(true);
                    return symbol;
                }

                System.out.println("Line " + astscalarelement.getBeginLine() + ": Variable " + astscalarelement.id +
                        " already declared.");
                return null;
            }

            //parse right hand side if existent
            boolean initialized = false;
            boolean sizeSet = false;
            //if is from type a=CONST;
            if(declarationTree.integer != null)
            {
                initialized = true;
                if(symbol!= null && symbol.getType().equals("ARRAY") && symbol.isSizeSet() == false)
                {
                    System.out.println("Line " + declarationTree.getBeginLine() + ": Variable " +
                            symbol.getId() + " has the size not defined." + "Error assigning " +
                            declarationTree.integer + " to all elements of " + symbol.getId() + ".");
                    return null;
                }
            }

            VarSymbol varSymbol = new VarSymbol(astscalarelement.id, SymbolType.INTEGER.toString(), initialized, sizeSet);

            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                if(astarraysize.integer == null) {
                    ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                    VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);

                    if (scalarAccessSymbol == null)
                        return null;
                }

                varSymbol.setType(SymbolType.ARRAY.toString());
                varSymbol.setInitialized(true);
                varSymbol.setSizeSet(true);
            }


            mySymbols.put(varSymbol.getId(), varSymbol);
            return varSymbol;
        }
        else if(child instanceof ASTARRAYELEMENT)
        {
            ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
            Symbol symbol = hasAccessToSymbol(astarrayelement.id);
            //if it has already been declared and its not just a initialization
            if(symbol != null && declarationTree.integer == null)
            {
                System.out.println("Line " + astarrayelement.getBeginLine() + ": Variable " + astarrayelement.id +
                        " already declared.");
                return null;
            }

            boolean initialized;
            boolean sizeSet;
            if(declarationTree.jjtGetNumChildren() > 1)
            {
                //if is from type a[]=[CONST];
                child = declarationTree.jjtGetChild(1);
                ASTARRAYSIZE astarraysize = (ASTARRAYSIZE)child;
                if(astarraysize.integer == null)
                {
                    ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                    VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);
                    if(scalarAccessSymbol == null)
                        return null;
                }
                initialized = true;
                sizeSet = true;
            }
            else
            {
                if(declarationTree.integer != null) //if is from type a[]=CONST and a[] have not been previously defined, it cannot happen
                {
                    if (symbol != null) //if is from type a[]=CONST and a[] have been previously defined
                    {
                        VarSymbol varSymbol = (VarSymbol) symbol;
                        if(varSymbol.isSizeSet() == false)
                        {
                            System.out.println("Line " + declarationTree.getBeginLine() + ": Variable " +
                                    astarrayelement.id + " has the size not defined." + "Error assigning " +
                                    declarationTree.integer + " to all elements of " + astarrayelement.id + ".");
                            return null;
                        }
                        varSymbol.setInitialized(true);
                        return varSymbol;
                    }
                }

                //if from type a[]; variable not initialized and size = -1
                initialized = false;
                sizeSet = false;
            }

            VarSymbol varSymbol = new VarSymbol(astarrayelement.id, SymbolType.ARRAY.toString(), initialized, sizeSet);

            mySymbols.put(varSymbol.getId(), varSymbol);
            return varSymbol;
        }

        return null;
    }

    private boolean parseAssign(ASTASSIGN assignTree)
    {
        VarSymbol rhsSymbol = null;
        SimpleNode rhsTree = (SimpleNode) assignTree.jjtGetChild(1);
        if(rhsTree != null)
            rhsSymbol = parseRhs(rhsTree);
        if(rhsSymbol == null)
            return false;

        SimpleNode lhsTree = (SimpleNode) assignTree.jjtGetChild(0);
        VarSymbol lhsSymbol = getLhsVariable(lhsTree);
        if(lhsSymbol == null)
            return false;

       if(rhsSymbol.getType().equals("ARRAYSIZE"))
       {
           if (lhsSymbol.getType().equals(SymbolType.ARRAY.toString()))
               return addToSymbolTable(lhsSymbol);
           else
               rhsSymbol.setType(SymbolType.ARRAY.toString());
       }

       if(lhsSymbol.getType().equals(SymbolType.UNDEFINED.toString()))
       {
           if(rhsSymbol.getType().equals(SymbolType.UNDEFINED.toString()))
               lhsSymbol.setType(SymbolType.INTEGER.toString());
           else
               lhsSymbol.setType(rhsSymbol.getType());
       }

        String lhsSymbolType = lhsSymbol.getType();
        String rhsSymbolType = rhsSymbol.getType();


        if(! (lhsSymbolType.equals(SymbolType.ARRAY.toString()) && rhsSymbolType.equals(SymbolType.INTEGER.toString()))) //for A=5; in which A is an array and all its elements are set to 5
            if(!rhsSymbolType.equals(SymbolType.UNDEFINED.toString())) //for A=m.f(); in which m.f() function is from another module that we not know the return value, so it can be INTEGER or ARRAY
                if(!lhsSymbolType.equals(rhsSymbolType))
                {
                    System.out.println("Line " + lhsTree.getBeginLine() + ": Variable " + lhsSymbol.getId() +
                            " has been declared as " + lhsSymbolType + ". Cannot redeclare it as " +
                            rhsSymbolType + ".");
                    return false;
                }

        lhsSymbol.setInitialized(true);

        if(lhsSymbol.getId().contains(".size")) {
            System.out.println("Impossible to assign a value to " + lhsSymbol.getId());
            return false;
        }

        return addToSymbolTable(lhsSymbol);
    }

    private boolean addToSymbolTable(VarSymbol lhsSymbol)
    {
        if((inheritedSymbols.get(lhsSymbol.getId()) == null) && (mySymbols.get(lhsSymbol.getId()) == null))
        {
            mySymbols.put(lhsSymbol.getId(), lhsSymbol);
            return true;
        }

        return false;
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
                symbol = (VarSymbol) hasAccessToSymbol(id);
                if(symbol == null)
                    return null;

                ASTINDEX astindex = (ASTINDEX) child.jjtGetChild(0);
                if(!parseIndex(astindex, symbol))
                    return null;
                symbol = symbol.getCopy(); //symbol type will be altered but only for this case, so we need a copy
                symbol.setType(SymbolType.INTEGER.toString());
                break;

            case "SCALARACCESS":
                id = ((ASTSCALARACCESS) child).id;
                symbol = (VarSymbol) hasAccessToSymbol(id);

                if(symbol == null)
                    symbol = new VarSymbol(id, SymbolType.UNDEFINED.toString(), false);

                break;
        }

        return symbol;
    }

    private boolean parseIndex(ASTINDEX astIndex, VarSymbol arraySymbol)
    {
        String indexSymbolId = astIndex.indexID;
        if (indexSymbolId != null)
        {
            VarSymbol indexSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(astIndex, indexSymbolId);
            return indexSymbol != null;
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
        VarSymbol rhsSymbol = parseRhs(astRhs);
        if(rhsSymbol == null)
            return false;

        if(!lhsSymbol.getType().equals(rhsSymbol.getType()))
        {
            System.out.println("Line " + astLhs.getBeginLine() + ": Variables must have same type to be compared." +
                    "Variable " + lhsSymbol.getId() + " has type " + lhsSymbol.getType() + " and variable " +
                    rhsSymbol.getId() + " has type " + rhsSymbol.getType() + ".");
            return false;
        }

        if(lhsSymbol.getType().equals(SymbolType.ARRAY.toString()))
        {
            System.out.println("Line " + astLhs.getBeginLine() + ": Variables must be INTEGER to be compared. Variable "
                    + lhsSymbol.getId() + " has type " + lhsSymbol.getType() + " and variable " + rhsSymbol.getId() +
                    " has type " + rhsSymbol.getType() + ".");
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
        HashMap<String, Symbol> symbolsNotInitialized = new HashMap<>();

        for (Object o : symbols.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            String symbolName = (String) pair.getKey();
            VarSymbol symbol = (VarSymbol) pair.getValue();
            symbol.setInitialized(false);
            symbolsNotInitialized.put(symbolName, symbol);
        }

        return symbolsNotInitialized;
    }

}
