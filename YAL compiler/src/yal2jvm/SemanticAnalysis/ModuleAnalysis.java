package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.FunctionSymbol;
import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.*;

import java.util.*;

public class ModuleAnalysis extends Analysis
{
    public static String moduleName;

    public ModuleAnalysis(SimpleNode ast)
    {
        super(ast, null, new HashMap<>());
        moduleName = ((ASTMODULE) ast).name;
    }

    private void initiateGlobalSymbolTable()
    {
        int numChildren = ast.jjtGetNumChildren();
        for(int i = 0; i < numChildren; i++)
        {
            SimpleNode child = (SimpleNode) ast.jjtGetChild(i);
            addSymbolToSymbolTable(child);
        }
    }

    public void parse()
    {
        initiateGlobalSymbolTable();

        HashMap<String, Symbol> unifiedSymbolTable = getUnifiedSymbolTable();
        for (Object o : functionNameToFunctionSymbol.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            FunctionSymbol functionSymbol = (FunctionSymbol) pair.getValue();

            SimpleNode functionAST = functionSymbol.getFunctionAST();
            FunctionAnalysis functionAnalysis = new FunctionAnalysis(functionAST, unifiedSymbolTable,
                    functionNameToFunctionSymbol);
            functionAnalysis.parse();
        }
    }

    private void addSymbolToSymbolTable(SimpleNode child)
    {
        String type = child.toString();

        switch (type)
        {
            case "FUNCTION":
                ASTFUNCTION astfunctionNode = (ASTFUNCTION) child;
                String functionId = astfunctionNode.id;
                FunctionSymbol functionSymbol = new FunctionSymbol(astfunctionNode, functionId);
                functionSymbol.parseFunctionHeader();

                functionNameToFunctionSymbol.put(functionSymbol.getId(), functionSymbol);
                break;
            case "DECLARATION":
                parseDeclaration((ASTDECLARATION) child);
                break;
            default:
                System.out.println("Line " + child.getBeginLine() + ": Unexpected node" + child.toString());
                System.exit(-1);
                break;
        }
    }
}
