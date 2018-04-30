package yal2jvm.semantic_analysis;

import yal2jvm.ast.*;
import yal2jvm.symbol_tables.FunctionSymbol;
import yal2jvm.symbol_tables.Symbol;
import yal2jvm.symbol_tables.VarSymbol;

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
        FunctionSymbol astFunction = (FunctionSymbol) functionNameToFunctionSymbol.get(((ASTFUNCTION) ast).id);

        addArgumentsToMySymbols(astFunction);
        addReturnValueToMySymbols(astFunction);

        int statementsChildNumber = astFunction.getStatementsChildNumber();
        ASTSTATEMENTS statementsNode = (ASTSTATEMENTS) ast.jjtGetChild(statementsChildNumber);
        parseStmtLst(statementsNode);

        //verify return value is defined if exists
        VarSymbol returnValue = astFunction.getReturnValue();
        if (returnValue != null)
        {
            if (!returnValue.isInitialized())
            {
                System.out.println("Line " + astFunction.getFunctionAST().getBeginLine() + ": Return variable " + returnValue.getId()
                        + " might not have been initialized. Function " + astFunction.getId() + " must have return variable initialized.");
                ModuleAnalysis.hasErrors = true;
            }
        }
    }

    private void addArgumentsToMySymbols(FunctionSymbol astFunction)
    {
        ArrayList<VarSymbol> arguments = astFunction.getArguments();
        for (VarSymbol argument : arguments)
            mySymbols.put(argument.getId(), argument);
    }

    private void addReturnValueToMySymbols(FunctionSymbol astFunction)
    {
        VarSymbol returnValue = astFunction.getReturnValue();
        if (returnValue != null)
            mySymbols.put(returnValue.getId(), returnValue);
    }

}
