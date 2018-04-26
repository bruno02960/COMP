package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.ASTEXPRTEST;
import yal2jvm.ast.ASTSTATEMENTS;
import yal2jvm.ast.SimpleNode;
import yal2jvm.SymbolTables.Symbol;

import java.util.HashMap;

public class WhileAnalysis extends Analysis
{
    public WhileAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                         HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
        super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    public void parse()
    {
        ASTEXPRTEST exprTest = ((ASTEXPRTEST) ast.jjtGetChild(0));
        parseExprTest(exprTest);

        //get inherited symbols States after while
        HashMap<String, Symbol> inheritedSymbolsHashMapAfterWhile = new HashMap<>(inheritedSymbols);

        ASTSTATEMENTS stmtlst = ((ASTSTATEMENTS) ast.jjtGetChild(1));
        parseStmtLst(stmtlst);

        //set inheritedSymbols as they were before while, because while can not be executed
        inheritedSymbols = inheritedSymbolsHashMapAfterWhile;

        //symbols created inside while are added to symbol table, but as not initialized, because while statements can not be executed
        mySymbols = setAllSymbolsAsNotInitialized(mySymbols);
    }

}
