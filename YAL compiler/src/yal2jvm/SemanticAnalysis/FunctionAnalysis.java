package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.*;

import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{
    public FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                            HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
       super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    protected void parse()
    {
        int astNumChilds = ast.jjtGetNumChildren();
        for(int i = 0; i < astNumChilds; i++)
        {
            SimpleNode node = (SimpleNode) ast.jjtGetChild(i);
            String nodeId = node.toString();
            switch(nodeId)
            {
                case "ASTWHILE":
                    WhileAnalysis whileAnalysis = new WhileAnalysis(ast, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    whileAnalysis.parse();
            }
        }
    }

}
