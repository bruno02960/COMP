package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.FunctionSymbol;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
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

        addArgumentsToMySymbols(astFunction);
        addReturnValueToMySymbols(astFunction);

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

        //verify return value is defined if exists
        VarSymbol returnValue = astFunction.getReturnValue();
        if(returnValue != null)
        {
            if(!returnValue.isInitialized())
                System.err.println("Function " + astFunction.getId() + " must have return variable " +
                        returnValue.getId() + " defined."); //TODO linha

        }
    }

    private void addArgumentsToMySymbols(FunctionSymbol astFunction)
    {
        ArrayList<VarSymbol> arguments = astFunction.getArguments();
        for(int i = 0; i < arguments.size(); i++)
            mySymbols.put(arguments.get(i).getId(), arguments.get(i));
    }

    private void addReturnValueToMySymbols(FunctionSymbol astFunction)
    {
       VarSymbol returnValue = astFunction.getReturnValue();
       if(returnValue != null)
           mySymbols.put(returnValue.getId(), returnValue);
    }


}
