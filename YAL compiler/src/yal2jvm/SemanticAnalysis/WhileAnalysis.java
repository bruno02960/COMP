package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.ASTEXPRTEST;
import yal2jvm.ast.ASTSTATEMENTS;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class WhileAnalysis extends Analysis
{
    public WhileAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                            HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
        super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    public void parse()
    {
        ASTEXPRTEST exprTest = ((ASTEXPRTEST) ast.jjtGetChild(0));
        parseExprTest(exprTest);

        //get symbols States Before While
        ArrayList<Symbol> symbolsStatesBeforeWhile = (ArrayList<Symbol>) inheritedSymbols.values();

        ASTSTATEMENTS stmtlst = ((ASTSTATEMENTS) ast.jjtGetChild(1));
        parseStmtLst(stmtlst);

        //get symbols States after While
        ArrayList<Symbol> symbolsStatesAfterWhile = (ArrayList<Symbol>) inheritedSymbols.values();

        //set as not initialized symbols that were initialized inside while, as its statements can not be executed
        assert symbolsStatesBeforeWhile.size() == symbolsStatesAfterWhile.size();
        for(int i = 0; i < symbolsStatesAfterWhile.size(); i++)
        {
            VarSymbol symbolBeforeWhile = (VarSymbol) symbolsStatesBeforeWhile.get(i);
            VarSymbol symbolAfterWhile = (VarSymbol) symbolsStatesAfterWhile.get(i);
            symbolAfterWhile.setType(symbolBeforeWhile.getType());
        }

        //TODO: ver mySimbols (que sao do while e nao da funçao) e dai por ou nao como inicializado nos simbolos da funçao
        //TODO: aqui na realidade nao se por nenhum como initializado
    }

}
