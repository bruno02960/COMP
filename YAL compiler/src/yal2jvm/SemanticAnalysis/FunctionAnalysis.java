package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.ASTARRAYACCESS;
import yal2jvm.ast.ASTSCALARACCESS;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.Symbol;

import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{
    private HashMap<String, Symbol> functionNameToFunctionSymbolOfModule;

    public FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                            HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
       super(ast, inheritedSymbols);
       this.functionNameToFunctionSymbolOfModule = functionNameToFunctionSymbolOfModule;
    }

    @Override
    protected void parse()
    {

    }


}
