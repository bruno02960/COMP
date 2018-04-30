package yal2jvm.semantic_analysis;

import yal2jvm.ast.ASTEXPRTEST;
import yal2jvm.ast.ASTSTATEMENTS;
import yal2jvm.ast.SimpleNode;
import yal2jvm.symbol_tables.Symbol;
import yal2jvm.utils.Utils;

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

        //get inherited symbols States before while, in order to not change original values.
        // Changes made inside while mus not be visible outside, because while can not be executed
        HashMap<String, Symbol> inheritedSymbolsHashMapBeforeWhile = Utils.copyHashMap(inheritedSymbols);
        inheritedSymbols = inheritedSymbolsHashMapBeforeWhile;

        ASTSTATEMENTS stmtlst = ((ASTSTATEMENTS) ast.jjtGetChild(1));
        parseStmtLst(stmtlst);

        //symbols created inside while are added to symbol table, but as not initialized, because while statements can not be executed
        mySymbols = setAllSymbolsAsNotInitialized(mySymbols);
    }

}
