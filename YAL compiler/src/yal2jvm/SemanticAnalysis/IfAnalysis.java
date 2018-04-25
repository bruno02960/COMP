package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

        //get inherited symbols States Before If
        HashMap<String, Symbol> inheritedSymbolsHashMapBeforeIf = new HashMap<String, Symbol>(inheritedSymbols);

        //TODO: remove
        //ArrayList<Symbol> inheritedSymbolsStatesBeforeIf = new ArrayList<Symbol>(inheritedSymbols.values());

        ASTSTATEMENTS astStatements = (ASTSTATEMENTS) ast.jjtGetChild(1);
        parseStmtLst(astStatements);

        ASTELSE astElse = null;

        if (ast.jjtGetNumChildren() > 2) {
            astElse = (ASTELSE) ast.jjtGetChild(2);
        }

        if(astElse != null)
        {
            //get inherited symbols States after If
            //TODO: remove
            //HashMap<String, Symbol> inheritedSymbolsHashMapAfterIf = new HashMap<String, Symbol>(inheritedSymbols);
            ArrayList<Symbol> inheritedSymbolsStatesAfterIf = new ArrayList<Symbol>(inheritedSymbols.values());
            //get my symbols States after If
            ArrayList<Symbol> mySymbolsStatesAfterIf = new ArrayList<Symbol>(mySymbols.values());

            //clear mySymbols and inherited symbols for else parse
            mySymbols = new HashMap<String, Symbol>();
            inheritedSymbols = inheritedSymbolsHashMapBeforeIf;

            ASTSTATEMENTS astElseStatements = (ASTSTATEMENTS) astElse.jjtGetChild(0);
            parseStmtLst(astElseStatements);

            //get inherited symbols States after else
            ArrayList<Symbol> inheritedSymbolsStatesAfterElse = new ArrayList<Symbol>(inheritedSymbols.values());
            //get my symbols States after else
            ArrayList<Symbol> mySymbolsStatesAfterElse = new ArrayList<Symbol>(mySymbols.values());

            //set mySymbols as the symbols declared in if and else
            HashMap<String, Symbol> newMySymbols = mergeDeclaredSymbols(mySymbolsStatesAfterIf, mySymbolsStatesAfterElse);
            newMySymbols = setAllSymbolsAsNotInitialized(newMySymbols);
            ArrayList<Symbol> commonDeclaredSymbols = getCommonDeclaredSymbols(mySymbolsStatesAfterIf, mySymbolsStatesAfterElse);
            mySymbols = setListSymbolsAsInitializedAccordingToOtherList(newMySymbols, commonDeclaredSymbols);

            ArrayList<Symbol> commonInitializedSymbols = getCommonInitializedSymbols(inheritedSymbolsStatesAfterIf, inheritedSymbolsStatesAfterElse);
            Iterator it = inheritedSymbols.entrySet().iterator();
            while(it.hasNext())
            {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                VarSymbol symbol = (VarSymbol) pair.getValue();
                if(commonInitializedSymbols.contains(symbol))
                    symbol.setInitialized(true);
            }
        }
        else
        {
            //set as not initialized symbols that were initialized inside if, as its statements can not be executed
            inheritedSymbols = inheritedSymbolsHashMapBeforeIf;

            //TODO: remove
            /*assert inheritedSymbolsStatesBeforeIf.size() == inheritedSymbols.size();
            for(int i = 0; i < inheritedSymbols.size(); i++)
            {
                VarSymbol symbolBeforeIf = (VarSymbol) inheritedSymbolsStatesBeforeIf.get(i);
                VarSymbol symbolAfterIf = (VarSymbol) inheritedSymbols.get(i);
                symbolAfterIf.setInitialized(symbolBeforeIf.isInitialized());
                if(symbolAfterIf.getType().equals("ARRAY"))
                    symbolAfterIf.setSize(symbolBeforeIf.getSize());
            }*/

            //symbols created inside while are added to symbol table, but as not initialized, because while statements can not be executed
            mySymbols = setAllSymbolsAsNotInitialized(mySymbols);
        }
    }

    private HashMap<String,Symbol> setListSymbolsAsInitializedAccordingToOtherList(HashMap<String, Symbol> symbols,
                                                                                   ArrayList<Symbol> commonDeclaredSymbols)
    {
        HashMap<String, Symbol> symbolsInitialized = new HashMap<String, Symbol>();

        Iterator it = symbols.entrySet().iterator();
        while(it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            VarSymbol symbol = (VarSymbol) pair.getValue();
            if(commonDeclaredSymbols.contains(symbol))
            {
                String symbolName = (String) pair.getKey();
                symbol.setInitialized(true);
                symbolsInitialized.put(symbolName, symbol);
            }
        }

        return symbolsInitialized;
    }

    private HashMap<String,Symbol> mergeDeclaredSymbols(ArrayList<Symbol> mySymbolsStatesAfterIf,
                                                        ArrayList<Symbol> mySymbolsStatesAfterElse)
    {
        HashMap<String,Symbol> mergedDeclaredSymbols = new HashMap<String,Symbol>();

        for(Symbol symbol : mySymbolsStatesAfterIf)
        {
            if(mergedDeclaredSymbols.containsKey(symbol.getId()) == false)
                mergedDeclaredSymbols.put(symbol.getId(), symbol);
        }

        for(Symbol symbol : mySymbolsStatesAfterElse)
        {
            if(mergedDeclaredSymbols.containsKey(symbol.getId()) == false)
                mergedDeclaredSymbols.put(symbol.getId(), symbol);
        }

        return  mergedDeclaredSymbols;
    }

    private ArrayList<Symbol> getCommonInitializedSymbols(ArrayList<Symbol> inheritedSymbolsStatesAfterIf,
                                                          ArrayList<Symbol> inheritedSymbolsStatesAfterElse)
    {
        assert inheritedSymbolsStatesAfterIf.size() == inheritedSymbolsStatesAfterElse.size();
        ArrayList<Symbol> commonInitializedSymbols = new ArrayList<Symbol>();
        for(int i = 0; i < inheritedSymbolsStatesAfterIf.size(); i++)
        {
            VarSymbol symbolAfterIf = (VarSymbol) inheritedSymbolsStatesAfterIf.get(i);
            VarSymbol symbolAfterElse = (VarSymbol) inheritedSymbolsStatesAfterElse.get(i);
            if(symbolAfterIf.isInitialized() && symbolAfterElse.isInitialized())
                commonInitializedSymbols.add(symbolAfterElse);
        }

        return commonInitializedSymbols;
    }

    private ArrayList<Symbol> getCommonDeclaredSymbols(ArrayList<Symbol> mySymbolsStatesAfterIf,
                                               ArrayList<Symbol> mySymbolsStatesAfterElse)
    {
        ArrayList<Symbol> commons = mySymbolsStatesAfterIf;
        commons.retainAll(mySymbolsStatesAfterElse);

        return commons;
    }

}
