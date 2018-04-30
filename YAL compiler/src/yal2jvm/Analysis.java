package yal2jvm;

import yal2jvm.HHIR.Type;
import yal2jvm.SemanticAnalysis.IfAnalysis;
import yal2jvm.SemanticAnalysis.ModuleAnalysis;
import yal2jvm.SemanticAnalysis.WhileAnalysis;
import yal2jvm.SymbolTables.*;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
        if (inheritedSymbols != null)
            unifiedSymbolTable.putAll(inheritedSymbols);
        if (mySymbols != null)
            unifiedSymbolTable.putAll(mySymbols);

        return unifiedSymbolTable;
    }

    private Symbol hasAccessToSymbol(String symbolId)
    {
        Symbol symbol = null;
        if (mySymbols != null)
        {
            symbol = mySymbols.get(symbolId);
            if (symbol != null)
                return symbol;
        }

        if (inheritedSymbols != null)
            symbol = inheritedSymbols.get(symbolId);

        return symbol;
    }

    private VarSymbol parseLhs(SimpleNode lhsTree)
    {
        Node child = lhsTree.jjtGetChild(0);
        switch (child.toString())
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
        if (firstChild.toString().equals("ARRAYSIZE"))
        {
            VarSymbol retVal = parseArraySize((ASTARRAYSIZE) firstChild);

            if (retVal == null)
            {
                //TODO: Shouldn't be an error message here?
                return null;
            }

            retVal = retVal.getCopy();
            retVal.setType("ARRAYSIZE");
            return retVal;
        }

        VarSymbol symbol = null;
        String previousType = null;
        int numChildren = rhsTree.jjtGetNumChildren();
        for (int i = 0; i < numChildren; i++)
        {
            ASTTERM child = (ASTTERM) rhsTree.jjtGetChild(i);
            VarSymbol previousSymbol = symbol;
            symbol = parseTerm(child);
            if (symbol == null)
                return null;
            String symbolType = symbol.getType();
            if (previousType == null)
                previousType = symbolType;
            else if (!previousType.equals(symbolType))
            {
                System.out.println("Line " + child.getBeginLine() + ": Variables dont match! Variable "
                        + previousSymbol.getId() + " has type " + previousSymbol.getType()
                        + " and " + symbol.getId() + " has type " + symbol.getType() + ".");
                ModuleAnalysis.hasErrors = true;
                return null;
            }
            else if (previousType.equals(Type.ARRAY.toString()) && symbolType.equals(Type.ARRAY.toString()))
            {
                System.out.println("Line " + child.getBeginLine() + ": Cannot make operations between arrays.");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

        }

        return symbol;
    }

    private VarSymbol parseArraySize(ASTARRAYSIZE arraySizeTree)
    {
        if (arraySizeTree.integer != null)
        {
            return new ImmediateSymbol("[" + arraySizeTree.integer + "]");
        } else
        {
            ASTSCALARACCESS child = (ASTSCALARACCESS) arraySizeTree.jjtGetChild(0);
            return parseScalarAccess(child);
        }
    }

    private VarSymbol parseTerm(ASTTERM termTree)
    {
        if (termTree.integer != null)
            return new ImmediateSymbol("[" + termTree.integer + "]");

        Node child = termTree.jjtGetChild(0);
        switch (child.toString())
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
        if (module != null && !module.equals(ModuleAnalysis.moduleName))
        {
            ArrayList<String> argumentsTypes = new ArrayList<>();
            if (callTree.jjtGetNumChildren() > 0)
            {
                ASTARGUMENTS astarguments = (ASTARGUMENTS) callTree.jjtGetChild(0);
                argumentsTypes = parseArgumentList(astarguments);

                if (argumentsTypes == null)
                    return null;
            }

            if (argumentsTypes.contains("STRING") && !module.equals("io"))
            {
                System.out.println("Line " + callTree.getBeginLine() + ": Only module io can have string type arguments.");
                ModuleAnalysis.hasErrors = true;
            }

            return new VarSymbol("", SymbolType.UNDEFINED.toString(), true);

        }

        String method = callTree.method;
        FunctionSymbol functionSymbol = (FunctionSymbol) functionNameToFunctionSymbol.get(method);
        if (functionSymbol == null)
        {
            System.out.println("Line " + callTree.getBeginLine() + ": Method " + method + " can´t be found.");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        ArrayList<VarSymbol> functionArguments = functionSymbol.getArguments();
        VarSymbol returnSymbol = functionSymbol.getReturnValue();

        if (callTree.jjtGetNumChildren() == 0)
        {
            if (callTree.jjtGetNumChildren() != functionArguments.size())
            {
                System.out.println("Line " + callTree.getBeginLine() + ": Method " + method + " arguments number(0)"
                        + "does not match expected number(" + functionArguments.size() + ") of arguments");
                ModuleAnalysis.hasErrors = true;
                return null;
            }
        } else
        {
            ASTARGUMENTS astarguments = (ASTARGUMENTS) callTree.jjtGetChild(0);
            ArrayList<String> argumentsTypes = parseArgumentList(astarguments);
            if (argumentsTypes == null)
                return null;

            if (functionArguments.size() != argumentsTypes.size())
            {
                System.out.println("Line " + astarguments.getBeginLine() + ": Method " + method + " arguments number("
                        + argumentsTypes.size() + ") does not match expected number(" + functionArguments.size() + ") of arguments");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

            for (int i = 0; i < functionArguments.size(); i++)
            {
                String argumentType = argumentsTypes.get(i);
                String expectedArgumentType = functionArguments.get(i).getType();
                if (!argumentType.equals(expectedArgumentType))
                {
                    System.out.println("Line " + astarguments.getBeginLine() + ": Type " + argumentType
                            + " of argument " + i + 1 + " of method " + method
                            + " call does not match expected type " + expectedArgumentType + ".");
                    ModuleAnalysis.hasErrors = true;
                    returnSymbol = null;
                }
            }
        }

        if (returnSymbol == null)
            return null;

        returnSymbol = returnSymbol.getCopy();
        returnSymbol.setInitialized(true);
        if (returnSymbol.getType().equals(Type.ARRAY.toString()))
            returnSymbol.setSizeSet(true);

        return returnSymbol;
    }

    private ArrayList<String> parseArgumentList(ASTARGUMENTS astarguments)
    {
        Integer childrenLength = astarguments.jjtGetNumChildren();
        ArrayList<String> argumentsTypes = new ArrayList<>();
        boolean haveFailed = false;
        for (int i = 0; i < childrenLength; i++)
        {
            ASTARGUMENT astargument = ((ASTARGUMENT) astarguments.jjtGetChild(i));
            String idArg = astargument.idArg;
            Integer intArg = astargument.intArg;
            String stringArg = astargument.stringArg;

            if (idArg == null && intArg == null && stringArg == null)
            {
                System.out.println("Line " + astargument.getBeginLine() + ": Argument " + i + " is neither a variable,"
                        + "a string or an integer.");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

            if (idArg != null)
            {
                VarSymbol varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(astargument, idArg);
                if (varSymbol == null)
                {
                    haveFailed = true;
                    continue;
                }
                argumentsTypes.add(varSymbol.getType());
            } else if (intArg != null)
                argumentsTypes.add(SymbolType.INTEGER.toString());
            else
                argumentsTypes.add("STRING");
        }

        if (haveFailed)
            return null;

        return argumentsTypes;
    }

    private VarSymbol parseArrayAccess(ASTARRAYACCESS arrayAccessTree)
    {
        String arrayId = arrayAccessTree.arrayID;

        VarSymbol arraySymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(arrayAccessTree, arrayId);
        if (arraySymbol == null)
            return null;

        if (!arraySymbol.getType().equals(SymbolType.ARRAY.toString()))
        {
            System.out.println("Line " + arrayAccessTree.getBeginLine() + ": Access to index of variable " + arrayId
                    + " that is not an array.");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        ASTINDEX astindex = (ASTINDEX) arrayAccessTree.jjtGetChild(0);
        String indexValue = null;

        if (astindex.indexValue != null)
            indexValue = astindex.indexValue.toString();

        String indexSymbolId = astindex.indexID;
        if (indexSymbolId != null)
        {
            VarSymbol indexSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(astindex, indexSymbolId);
            if (indexSymbol == null)
                return null;
            indexValue = indexSymbol.getId();
        }

        arraySymbol = arraySymbol.getCopy();
        arraySymbol.setType(SymbolType.INTEGER.toString());
        arraySymbol.setId(arraySymbol.getId() + "[" + indexValue + "]");

        return arraySymbol;
    }

    private Symbol checkSymbolExistsAndIsInitialized(SimpleNode ast, String symbolId)
    {
        VarSymbol indexSymbol = (VarSymbol) hasAccessToSymbol(symbolId);
        if (indexSymbol == null)
        {
            System.out.println("Line " + ast.getBeginLine() + ": Variable " + symbolId + " might not have been declared.");
            ModuleAnalysis.hasErrors = true;
            return null;
        }
        if (!indexSymbol.isInitialized())
        {
            System.out.println("Line " + ast.getBeginLine() + ": Variable " + symbolId + " might not have been initialized.");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        return indexSymbol;
    }

    private VarSymbol parseScalarAccess(ASTSCALARACCESS scalarAccessTree)
    {
        String id = scalarAccessTree.id;
        boolean sizeAccess = false;
        if (id.contains("."))
        {
            int dotIdx = id.indexOf(".");
            if (id.substring(dotIdx + 1).equals("size"))
                sizeAccess = true;
            id = id.substring(0, dotIdx);
        }

        VarSymbol varSymbol;
        if (sizeAccess)
            varSymbol = (VarSymbol) hasAccessToSymbol(id);
        else
            varSymbol = (VarSymbol) checkSymbolExistsAndIsInitialized(scalarAccessTree, id);

        if (varSymbol == null)
            return null;

        if (sizeAccess)
        {
            if (varSymbol.getType().equals("INTEGER"))
            {
                System.out.println("Line " + scalarAccessTree.getBeginLine() + ": Access to size of variable " + id
                        + " that is not an array.");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

            if (varSymbol.getType().equals("ARRAY") && varSymbol.isSizeSet() == false)
            {
                System.out.println("Line " + scalarAccessTree.getBeginLine() + ": Cannot access to array " + id
                        + " size, because size is undefined.");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

            varSymbol = varSymbol.getCopy();
            varSymbol.setType(SymbolType.INTEGER.toString());
        }

        return varSymbol;
    }

    protected VarSymbol parseDeclaration(ASTDECLARATION declarationTree)
    {
        Node child = declarationTree.jjtGetChild(0);
        if (child instanceof ASTSCALARELEMENT)
        {
            ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) child;
            return parseDeclarationAstScalarElement(declarationTree, astscalarelement);

        } else if (child instanceof ASTARRAYELEMENT)
        {
            ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) child;
            return parseDeclarationAstArrayElement(declarationTree, astarrayelement);
        }

        return null;
    }

    private VarSymbol parseDeclarationAstScalarElement(ASTDECLARATION declarationTree, ASTSCALARELEMENT astscalarelement)
    {
        VarSymbol symbol = (VarSymbol) hasAccessToSymbol(astscalarelement.id);
        if (symbol != null)
            return parseDeclarationSymbol(declarationTree, symbol);

        VarSymbol varSymbol = createSymbolForDeclarationAstScalarElement(declarationTree, astscalarelement);
        if (varSymbol == null)
            return null;

        mySymbols.put(varSymbol.getId(), varSymbol);
        return varSymbol;
    }

    private VarSymbol createSymbolForDeclarationAstScalarElement(ASTDECLARATION declarationTree, ASTSCALARELEMENT astscalarelement)
    {
        boolean initialized = false;
        if (declarationTree.integer != null) //if is from type a=CONST;
            initialized = true;

        VarSymbol varSymbol = new VarSymbol(astscalarelement.id, SymbolType.INTEGER.toString(), initialized, false);

        if (declarationTree.jjtGetNumChildren() > 1) //if is from type a=[CONST];
        {
            Node child = declarationTree.jjtGetChild(1);
            ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) child;
            if (astarraysize.integer == null)
            {
                ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);

                if (scalarAccessSymbol == null)
                    return null;
            }

            varSymbol.setType(SymbolType.ARRAY.toString());
            varSymbol.setSizeSet(true);
        }

        return varSymbol;
    }

    private VarSymbol parseDeclarationAstArrayElement(ASTDECLARATION declarationTree, ASTARRAYELEMENT astarrayelement)
    {
        VarSymbol symbol = (VarSymbol) hasAccessToSymbol(astarrayelement.id);
        if (symbol != null)
            return parseDeclarationSymbol(declarationTree, symbol);

        VarSymbol varSymbol = createSymbolForDeclarationAstArrayElement(declarationTree, astarrayelement);
        if (varSymbol == null)
            return null;

        mySymbols.put(varSymbol.getId(), varSymbol);
        return varSymbol;
    }

    private VarSymbol createSymbolForDeclarationAstArrayElement(ASTDECLARATION declarationTree, ASTARRAYELEMENT astarrayelement)
    {
        boolean initialized;
        boolean sizeSet;
        if (declarationTree.jjtGetNumChildren() > 1) //if is from type a[]=[CONST];
        {
            Node child = declarationTree.jjtGetChild(1);
            ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) child;
            if (astarraysize.integer == null)
            {
                ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                VarSymbol scalarAccessSymbol = parseScalarAccess(astScalarAccess);
                if (scalarAccessSymbol == null)
                    return null;
            }
            initialized = false;
            sizeSet = true;
        } else
        {
            //if from type a[] = CONST; and variable array has no size set (its not declared even)
            if (declarationTree.integer != null)
            {
                System.out.println("Line " + declarationTree.getBeginLine() + ": Variable "
                        + astarrayelement.id + " has the size not defined." + " Error assigning "
                        + declarationTree.integer + " to all elements of " + astarrayelement.id + ".");
                ModuleAnalysis.hasErrors = true;
                return null;
            }

            //if from type a[]; variable not initialized and size = -1
            initialized = false;
            sizeSet = false;
        }

        return new VarSymbol(astarrayelement.id, SymbolType.ARRAY.toString(), initialized, sizeSet);
    }

    private VarSymbol parseDeclarationSymbol(ASTDECLARATION declarationTree, VarSymbol symbol)
    {
        //if it has already been declared and its not just a initialization
        if (declarationTree.integer == null)
        {
            System.out.println("Line " + declarationTree.getBeginLine() + ": Variable " + symbol.getId()
                    + " already declared.");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        if (symbol.getType().equals(Type.INTEGER.toString()) && symbol.isInitialized())
        {
            System.out.println("Line " + declarationTree.getBeginLine() + ": Variable "
                    + symbol.getId() + " was already initialized." + " Error assigning "
                    + declarationTree.integer + " to the variable " + symbol.getId() + ".");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        symbol.setInitialized(true);

        if (symbol.getType().equals(Type.ARRAY.toString()) && symbol.isSizeSet() == false)
        {
            System.out.println("Line " + declarationTree.getBeginLine() + ": Variable "
                    + symbol.getId() + " has the size not defined." + " Error assigning "
                    + declarationTree.integer + " to all elements of " + symbol.getId() + ".");
            ModuleAnalysis.hasErrors = true;
            return null;
        }

        return symbol;
    }

    private boolean parseAssign(ASTASSIGN assignTree)
    {
        VarSymbol rhsSymbol = null;
        SimpleNode rhsTree = (SimpleNode) assignTree.jjtGetChild(1);
        if (rhsTree != null)
            rhsSymbol = parseRhs(rhsTree);

        SimpleNode lhsTree = (SimpleNode) assignTree.jjtGetChild(0);
        VarSymbol lhsSymbol = getLhsVariable(lhsTree);
        if (lhsSymbol == null)
            return false;

        //if rhs has an error, but lhs is correct, we assume that lhs is initialized
        if (rhsSymbol == null)
        {
            lhsSymbol.setInitialized(true);
            if (lhsSymbol.getType().equals("ARRAY"))
                lhsSymbol.setSizeSet(true);
            addToSymbolTable(lhsSymbol);

            return false;
        }

        if (lhsSymbol.getId().contains(".size"))
        {
            System.out.println("Line " + rhsTree.getBeginLine() + ": Impossible to set a variable size.");
            ModuleAnalysis.hasErrors = true;
            return false;
        }

        if (rhsSymbol.getType().equals("ARRAYSIZE"))
        {
            if (lhsSymbol.getType().equals(SymbolType.ARRAY.toString()) && lhsSymbol.isSizeSet()) // if is from type A = [VALUE] with A already declared
            {
                System.out.println("Line " + rhsTree.getBeginLine() + ": Variable " + lhsSymbol.getId() + " already declared.");
                ModuleAnalysis.hasErrors = true;
                return false;
            } else if (lhsSymbol.getType().equals(SymbolType.UNDEFINED.toString())) // if is from type A = [VALUE] with A still not declared
            {
                lhsSymbol.setType(SymbolType.ARRAY.toString());
                lhsSymbol.setSizeSet(true);
                return addToSymbolTable(lhsSymbol);
            }
        }

        if (lhsSymbol.getType().equals(SymbolType.UNDEFINED.toString()))
        {
            if (rhsSymbol.getType().equals(SymbolType.UNDEFINED.toString()))
                lhsSymbol.setType(SymbolType.INTEGER.toString());
            else
                lhsSymbol.setType(rhsSymbol.getType());
        }

        String lhsSymbolType = lhsSymbol.getType();
        String rhsSymbolType = rhsSymbol.getType();

        if (lhsSymbolType.equals(rhsSymbolType) && !(rhsSymbol instanceof ImmediateSymbol)) //if both lhs and rhs have same type
        {
            lhsSymbol.setSizeSet(rhsSymbol.isSizeSet());
            lhsSymbol.setInitialized(rhsSymbol.isInitialized());
            return addToSymbolTable(lhsSymbol);
        }

        //for the case in which the array as not the size defined yet
        if (lhsSymbolType.equals(SymbolType.ARRAY.toString()) && rhsSymbolType.equals("INTEGER")
                && lhsSymbol.isSizeSet() == false)
        {
            System.out.println("Line " + lhsTree.getBeginLine() + ": Variable " + lhsSymbol.getId()
                    + " has the size not defined." + " Error assigning right hand side to all elements of " + lhsSymbol.getId() + ".");
            ModuleAnalysis.hasErrors = true;
            return false;
        }

        //for A=[N] in which N is an integer. Used when assigning size to an array
        if (lhsSymbolType.equals(SymbolType.ARRAY.toString()) && rhsSymbolType.equals("ARRAYSIZE"))
        {
            lhsSymbol.setSizeSet(true);
            return addToSymbolTable(lhsSymbol);
        }

        if (!(lhsSymbolType.equals(SymbolType.ARRAY.toString()) && rhsSymbolType.equals(SymbolType.INTEGER.toString()))) //for A=5; in which A is an array and all its elements are set to 5
            if (!rhsSymbolType.equals(SymbolType.UNDEFINED.toString())) //for A=m.f(); in which m.f() function is from another module that we not know the return value, so it can be INTEGER or ARRAY
                if (!lhsSymbolType.equals(rhsSymbolType)) //checks both have types that match
                {
                    System.out.println("Line " + lhsTree.getBeginLine() + ": Variable " + lhsSymbol.getId()
                            + " has been declared as " + lhsSymbolType + ". Cannot redeclare it as " + rhsSymbolType + ".");
                    ModuleAnalysis.hasErrors = true;
                    return false;
                }

        lhsSymbol.setInitialized(true);

        return addToSymbolTable(lhsSymbol);
    }

    private boolean addToSymbolTable(VarSymbol lhsSymbol)
    {
        if ((inheritedSymbols.get(lhsSymbol.getId()) == null) && (mySymbols.get(lhsSymbol.getId()) == null))
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
        switch (child.toString())
        {
            case "ARRAYACCESS":
                id = ((ASTARRAYACCESS) child).arrayID;
                symbol = (VarSymbol) hasAccessToSymbol(id);
                if (symbol == null)
                    return null;

                ASTINDEX astindex = (ASTINDEX) child.jjtGetChild(0);
                if (!parseIndex(astindex))
                    return null;
                break;

            case "SCALARACCESS":
                id = ((ASTSCALARACCESS) child).id;
                symbol = (VarSymbol) hasAccessToSymbol(id);

                if (symbol == null)
                    symbol = new VarSymbol(id, SymbolType.UNDEFINED.toString(), false);

                break;
        }

        return symbol;
    }

    private boolean parseIndex(ASTINDEX astIndex)
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
        if (lhsSymbol == null)
            return false;

        ASTRHS astRhs = (ASTRHS) astExprtest.jjtGetChild(1);
        VarSymbol rhsSymbol = parseRhs(astRhs);
        if (rhsSymbol == null)
            return false;

        if (!lhsSymbol.getType().equals(rhsSymbol.getType()))
        {
            System.out.println("Line " + astLhs.getBeginLine() + ": Variables must have same type to be compared."
                    + "Variable " + lhsSymbol.getId() + " has type " + lhsSymbol.getType() + " and variable "
                    + rhsSymbol.getId() + " has type " + rhsSymbol.getType() + ".");
            ModuleAnalysis.hasErrors = true;
            return false;
        }

        if (lhsSymbol.getType().equals(SymbolType.ARRAY.toString()))
        {
            System.out.println("Line " + astLhs.getBeginLine() + ": Variables must be INTEGER to be compared. Variable "
                    + lhsSymbol.getId() + " has type " + lhsSymbol.getType() + " and variable " + rhsSymbol.getId()
                    + " has type " + rhsSymbol.getType() + ".");
            ModuleAnalysis.hasErrors = true;
            return false;
        }

        return true;
    }

    protected void parseStmtLst(ASTSTATEMENTS astStatements)
    {
        int statementsNumChilds = astStatements.jjtGetNumChildren();
        for (int i = 0; i < statementsNumChilds; i++)
        {
            SimpleNode node = (SimpleNode) astStatements.jjtGetChild(i);
            String nodeId = node.toString();
            switch (nodeId)
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

    protected HashMap<String, Symbol> setAllSymbolsAsNotInitialized(HashMap<String, Symbol> symbols)
    {
        HashMap<String, Symbol> symbolsNotInitialized = new HashMap<>();

        for (Entry<String, Symbol> o : symbols.entrySet())
        {
            HashMap.Entry<String, Symbol> pair = o;
            String symbolName = (String) pair.getKey();
            VarSymbol symbol = (VarSymbol) pair.getValue();
            symbol.setInitialized(false);
            symbolsNotInitialized.put(symbolName, symbol);
        }

        return symbolsNotInitialized;
    }

}
