package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.ASTEXPRTEST;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.Symbol;

import java.util.HashMap;

public class WhileAnalysis extends Analysis
{
    public WhileAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                            HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
        super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    protected void parse()
    {
        ASTEXPRTEST exprtest = ((ASTEXPRTEST) ast.jjtGetChild(0));

        SimpleNode lhs = (SimpleNode) exprtest.jjtGetChild(0);
        //return parseLhs(lhs);
    }
}
