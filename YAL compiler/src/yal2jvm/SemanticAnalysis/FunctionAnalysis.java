package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.SimpleNode;

import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{

    public FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
       super(ast, inheritedSymbols);
    }

    @Override
    protected void parse()
    {

    }
}
