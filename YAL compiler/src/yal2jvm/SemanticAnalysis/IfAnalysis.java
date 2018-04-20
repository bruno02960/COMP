package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.Symbol;

import java.util.HashMap;

public class IfAnalysis extends Analysis
{
    public IfAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                         HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
        super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    protected void parse()
    {

    }
}
