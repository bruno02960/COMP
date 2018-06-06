package yal2jvm.semantic_analysis;

import yal2jvm.Yal2jvm;
import yal2jvm.ast.*;
import yal2jvm.symbol_tables.FunctionSymbol;
import yal2jvm.symbol_tables.Symbol;
import yal2jvm.symbol_tables.VarSymbol;

import java.util.*;

/**
 *
 */
public class ModuleAnalysis extends Analysis
{
	public static boolean hasErrors;

	/**
	 *
	 * @param ast
	 */
	public ModuleAnalysis(SimpleNode ast)
	{
		super(ast, null, new HashMap<>());
		Yal2jvm.moduleName = ((ASTMODULE) ast).name;
	}

	/**
	 *
	 */
	public void parse()
	{
		initiateGlobalSymbolTable();
		setGlobalVariablesAsInitialized();

		HashMap<String, Symbol> unifiedSymbolTable = getUnifiedSymbolTable();
		for (HashMap.Entry<String, Symbol> o : functionNameToFunctionSymbol.entrySet())
		{
			HashMap.Entry<String, Symbol> pair = o;
			FunctionSymbol functionSymbol = (FunctionSymbol) pair.getValue();

			SimpleNode functionAST = functionSymbol.getFunctionAST();
			FunctionAnalysis functionAnalysis = new FunctionAnalysis(functionAST, unifiedSymbolTable,
					functionNameToFunctionSymbol);
			functionAnalysis.parse();
		}
	}

	/**
	 *
	 */
	private void initiateGlobalSymbolTable()
	{
		int numChildren = ast.jjtGetNumChildren();
		for (int i = 0; i < numChildren; i++)
		{
			SimpleNode child = (SimpleNode) ast.jjtGetChild(i);
			addSymbolToSymbolTable(child);
		}
	}

	/**
	 *
	 */
	private void setGlobalVariablesAsInitialized()
	{
		for (HashMap.Entry<String, Symbol> o : mySymbols.entrySet())
		{
			HashMap.Entry<String, Symbol> pair = o;
			VarSymbol symbol = (VarSymbol) pair.getValue();
			symbol.setInitialized(true);
		}
	}

	/**
	 *
	 * @param child
	 */
	private void addSymbolToSymbolTable(SimpleNode child)
	{
		String type = child.toString();

		switch (type)
		{
		case "FUNCTION":
			ASTFUNCTION astfunctionNode = (ASTFUNCTION) child;
			String functionId = astfunctionNode.id;
			FunctionSymbol functionSymbol = new FunctionSymbol(astfunctionNode, functionId);
			functionSymbol.parseFunctionHeader();
			addFunctionToHashMap(astfunctionNode, functionSymbol);
			break;

		case "DECLARATION":
			parseDeclaration((ASTDECLARATION) child);
			break;

		default:
			System.out.println("Line " + child.getBeginLine() + ": Unexpected node" + child.toString());
			System.exit(-1);
			break;
		}
	}

	/**
	 *
	 * @param astfunctionNode
	 * @param functionSymbol
	 * @return
	 */
	private boolean addFunctionToHashMap(ASTFUNCTION astfunctionNode, FunctionSymbol functionSymbol)
	{
		FunctionSymbol retValue = (FunctionSymbol) functionNameToFunctionSymbol.put(functionSymbol.getId(),
				functionSymbol);
		if (retValue != null)
		{
			functionNameToFunctionSymbol.put(retValue.getId(), retValue);
			System.out.println("Line " + astfunctionNode.getBeginLine() + ": Function " + functionSymbol.getId()
					+ " already declared.");
			ModuleAnalysis.hasErrors = true;
			return false;
		}

		return true;
	}
}
