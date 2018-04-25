package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
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

        //get inherited symbols States Before If
        HashMap<String, Symbol> inheritedSymbolsHashMapBeforeIf = new HashMap<>(inheritedSymbols);

        ASTSTATEMENTS astStatements = (ASTSTATEMENTS) ast.jjtGetChild(1);
        parseStmtLst(astStatements);

        ASTELSE astElse = null;

        if (ast.jjtGetNumChildren() > 2) {
            astElse = (ASTELSE) ast.jjtGetChild(2);
        }

        if(astElse != null)
        {
            //get inherited symbols States after If
            ArrayList<Symbol> inheritedSymbolsStatesAfterIf = new ArrayList<>(inheritedSymbols.values());
            //get my symbols States after If
            ArrayList<Symbol> mySymbolsStatesAfterIf = new ArrayList<>(mySymbols.values());

            //clear mySymbols and inherited symbols for else parse
            mySymbols = new HashMap<>();
            inheritedSymbols = inheritedSymbolsHashMapBeforeIf;

            ASTSTATEMENTS astElseStatements = (ASTSTATEMENTS) astElse.jjtGetChild(0);
            parseStmtLst(astElseStatements);

            //get inherited symbols States after else
            ArrayList<Symbol> inheritedSymbolsStatesAfterElse = new ArrayList<>(inheritedSymbols.values());
            //get my symbols States after else
            ArrayList<Symbol> mySymbolsStatesAfterElse = new ArrayList<>(mySymbols.values());

            //set mySymbols as the symbols declared in if and else
            HashMap<String, Symbol> newMySymbols = mergeDeclaredSymbols(mySymbolsStatesAfterIf, mySymbolsStatesAfterElse);
            newMySymbols = setAllSymbolsAsNotInitialized(newMySymbols);
            ArrayList<Symbol> commonDeclaredSymbols = getCommonDeclaredSymbols(mySymbolsStatesAfterIf, mySymbolsStatesAfterElse);
            mySymbols = setListSymbolsAsInitializedAccordingToOtherList(newMySymbols, commonDeclaredSymbols);

            ArrayList<Symbol> commonInitializedSymbols = getCommonInitializedSymbols(inheritedSymbolsStatesAfterIf, inheritedSymbolsStatesAfterElse);
            for (Object o : inheritedSymbols.entrySet()) {
                HashMap.Entry pair = (HashMap.Entry) o;
                VarSymbol symbol = (VarSymbol) pair.getValue();
                if (commonInitializedSymbols.contains(symbol))
                    symbol.setInitialized(true);
            }
        }
        else
        {
            //set as not initialized symbols that were initialized inside if, as its statements can not be executed
            inheritedSymbols = inheritedSymbolsHashMapBeforeIf;

            //symbols created inside while are added to symbol table, but as not initialized, because while statements can not be executed
            mySymbols = setAllSymbolsAsNotInitialized(mySymbols);
        }
    }

    private HashMap<String,Symbol> setListSymbolsAsInitializedAccordingToOtherList(HashMap<String, Symbol> symbols,
                                                                                   ArrayList<Symbol> commonDeclaredSymbols)
    {
        HashMap<String, Symbol> symbolsInitialized = new HashMap<>();

        for (Object o : symbols.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            VarSymbol symbol = (VarSymbol) pair.getValue();
            if (commonDeclaredSymbols.contains(symbol)) {
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
        HashMap<String,Symbol> mergedDeclaredSymbols = new HashMap<>();

        for(Symbol symbol : mySymbolsStatesAfterIf)
        {
            if(!mergedDeclaredSymbols.containsKey(symbol.getId()))
                mergedDeclaredSymbols.put(symbol.getId(), symbol);
        }

        for(Symbol symbol : mySymbolsStatesAfterElse)
        {
            if(!mergedDeclaredSymbols.containsKey(symbol.getId()))
                mergedDeclaredSymbols.put(symbol.getId(), symbol);
        }

        return  mergedDeclaredSymbols;
    }

    private ArrayList<Symbol> getCommonInitializedSymbols(ArrayList<Symbol> inheritedSymbolsStatesAfterIf,
                                                          ArrayList<Symbol> inheritedSymbolsStatesAfterElse)
    {
        assert inheritedSymbolsStatesAfterIf.size() == inheritedSymbolsStatesAfterElse.size();
        ArrayList<Symbol> commonInitializedSymbols = new ArrayList<>();
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
        ArrayList<Symbol> commons = new ArrayList<>();

        //number of common symbols is the minimum of arrays size
        int numCommonSymbols = mySymbolsStatesAfterIf.size();
        ArrayList<Symbol> symbolsBeingIterated = mySymbolsStatesAfterIf;
        ArrayList<Symbol> symbolsBeingChecked = mySymbolsStatesAfterElse;
        if(numCommonSymbols > mySymbolsStatesAfterElse.size())
        {
            numCommonSymbols = mySymbolsStatesAfterElse.size();
            symbolsBeingIterated = mySymbolsStatesAfterElse;
            symbolsBeingChecked = mySymbolsStatesAfterIf;
        }

        for(int i = 0; i < numCommonSymbols; i++)
        {
            VarSymbol symbolIterated = (VarSymbol) symbolsBeingIterated.get(i);
            int symbolIndex = symbolsBeingChecked.indexOf(symbolIterated);
            if(symbolIndex != -1)
            {
                VarSymbol symbolChecked = (VarSymbol) symbolsBeingChecked.get(symbolIndex);
                if(!symbolChecked.getType().equals(symbolIterated.getType()))
                    symbolIterated.setType("UNDEFINED");

                commons.add(symbolIterated);
            }
        }

        return commons;
    }

}
