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
                    WhileAnalysis whileAnalysis = new WhileAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    whileAnalysis.parse();
                    break;
                case "ASTIF":
                    IfAnalysis ifAnalysis = new IfAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    ifAnalysis.parse();
                    break;
                case "ASTCALL":
                    parseCall((ASTCALL) node);
                    break;
                case "ASTASSIGN":
                    parseAssign((ASTASSIGN) node);
                    break;
            }
        }
    }

}
