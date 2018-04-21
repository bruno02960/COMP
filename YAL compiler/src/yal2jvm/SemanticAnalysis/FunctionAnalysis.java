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
        FunctionSymbol astFunction = (FunctionSymbol) functionNameToFunctionSymbol.get(((ASTFUNCTION)ast).id);

        /* parseStmtlst() */
        int statementsChildNumber = astFunction.getStatementsChildNumber();

        Node statementsNode = ast.jjtGetChild(statementsChildNumber);
        int statementsNumChilds = statementsNode.jjtGetNumChildren();
        for(int i = 0; i < statementsNumChilds; i++)
        {
            SimpleNode node = (SimpleNode) statementsNode.jjtGetChild(i);
            String nodeId = node.toString();
            switch(nodeId)
            {
                case "WHILE":
                    WhileAnalysis whileAnalysis = new WhileAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    whileAnalysis.parse();
                    break;
                case "IF":
                    IfAnalysis ifAnalysis = new IfAnalysis(node, getUnifiedSymbolTable(), functionNameToFunctionSymbol);
                    ifAnalysis.parse();
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

}
