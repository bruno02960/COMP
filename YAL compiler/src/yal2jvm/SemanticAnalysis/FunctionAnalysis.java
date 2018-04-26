package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.FunctionSymbol;
import yal2jvm.SymbolTables.Symbol;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionAnalysis extends Analysis
{
    FunctionAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
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

        int statementsChildNumber = astFunction.getStatementsChildNumber();
        ASTSTATEMENTS statementsNode = (ASTSTATEMENTS) ast.jjtGetChild(statementsChildNumber);
        parseStmtLst(statementsNode);

        //verify return value is defined if exists
        VarSymbol returnValue = astFunction.getReturnValue();
        if(returnValue != null)
        {
            if(!returnValue.isInitialized())
                System.out.println("Line " + astFunction.getFunctionAST().getBeginLine() + ": Function " + astFunction.getId()
                        + " must have return variable " + returnValue.getId() + " defined.");
        }
    }

    private void addArgumentsToMySymbols(FunctionSymbol astFunction)
    {
        ArrayList<VarSymbol> arguments = astFunction.getArguments();
        for (VarSymbol argument : arguments) mySymbols.put(argument.getId(), argument);
    }

    private void addReturnValueToMySymbols(FunctionSymbol astFunction)
    {
       VarSymbol returnValue = astFunction.getReturnValue();
       if(returnValue != null)
           mySymbols.put(returnValue.getId(), returnValue);
    }


}
