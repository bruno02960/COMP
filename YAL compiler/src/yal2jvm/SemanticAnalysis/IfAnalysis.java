package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.ast.*;

import java.util.HashMap;

public class IfAnalysis extends Analysis
{
    public IfAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols,
                         HashMap<String, Symbol> functionNameToFunctionSymbolOfModule)
    {
        super(ast, inheritedSymbols, functionNameToFunctionSymbolOfModule);
    }

    @Override
    public void parse()
    {
        ASTEXPRTEST astExprtest = (ASTEXPRTEST) ast.jjtGetChild(0);
        parseExprTest(astExprtest);

        ASTSTATEMENTS astStatements = (ASTSTATEMENTS) ast.jjtGetChild(1);
        parseStmtLst(astStatements);

        //TODO: ver mySimbols (que sao do while e nao da funçao) e dai por ou nao como inicializado nos simbolos da funçao
        //TODO: por se no else e no if

    }

}
