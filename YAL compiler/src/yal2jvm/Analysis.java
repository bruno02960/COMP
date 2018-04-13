package yal2jvm;

import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.SimpleNode;

import java.util.HashMap;

public class Analysis
{
    protected HashMap<String, Symbol> mySymbols;
    protected HashMap<String, Symbol> inheritedSymbols;
    protected SimpleNode ast;
}
