package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.ASTEXPRTEST;
import yal2jvm.ast.ASTSTATEMENTS;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.Symbol;

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

        ASTSTATEMENTS stmtlst = ((ASTSTATEMENTS) ast.jjtGetChild(1));
        parseStmtLst(stmtlst);

        //TODO: ver mySimbols (que sao do while e nao da funçao) e dai por ou nao como inicializado nos simbolos da funçao
        //TODO: aqui na realidade nao se por nenhum como initializado
    }
}
