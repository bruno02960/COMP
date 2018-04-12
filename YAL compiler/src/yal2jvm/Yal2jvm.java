package yal2jvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import yal2jvm.SymbolTables.Symbol;
import yal2jvm.SymbolTables.SymbolTable;
import yal2jvm.ast.*;

public class Yal2jvm
{
	private int localVars;
	private boolean optimize;
	private String inputFile;
	private SimpleNode ast;
	private SymbolTable globalsSymbols = new SymbolTable();

	public Yal2jvm(int localVars, boolean optimize, String inputFile)
	{
		this.localVars = localVars;
		this.optimize = optimize;
		this.inputFile = inputFile;
	}
	
	public static void main(String args[])
	{
		String inputFile = null;
		boolean optimize = false;
		int localVars = 0;
		boolean validInput = true;
		
		switch (args.length)
		{
		case 0:
			validInput = false;
			break;
		case 1:
			inputFile = args[0];
			break;
		case 2:
			if (args[0].equals("-o"))
				optimize = true;
			else if (args[0].startsWith("-r"))
				localVars = Integer.parseInt(args[0].split("=")[1]);
			else
				validInput = false;
			inputFile = args[1];
			break;
		case 3:
			if (args[0].equals("-o") && args[1].startsWith("-r"))
			{
				optimize = true;
				localVars = Integer.parseInt(args[1].split("=")[1]);
			}
			else if (args[1].equals("-o") && args[0].startsWith("-r"))
			{
				optimize = true;
				localVars = Integer.parseInt(args[0].split("=")[1]);
			}
			else
				validInput = false;
			inputFile = args[1];
			break;
		default:
			validInput = false;
			break;
		}
		
		if (inputFile != null && validInput)
			validInput = inputFile.endsWith(".yal");
		
		if (localVars > 255)
			validInput = false;
		
		if (!validInput)
		{
			System.out.println("Insufficient or incorrect arguments for the Yal2jvm compiler");
			System.out.println("\nUsage:\tjava Yal2jvm [-r=<0..255>] [-o] <input_file.yal>\n");
		}
		else
		{
			Yal2jvm instance = new Yal2jvm(localVars, optimize, inputFile);
			instance.run();
		}
	}

	public void run()
	{
		FileInputStream inputStream = getFileStream();
		
		ast = createAst(inputStream);
		ast.dump("");

		initiateGlobalSymbolTable();
	}
	
	private void initiateGlobalSymbolTable()
	{
		int numChildren = ast.jjtGetNumChildren();
		
		for(int i = 0; i < numChildren; i++)
		{
			Node child = ast.jjtGetChild(i);
			Symbol symbol = createSymbol(child);
			globalsSymbols.addSymbolAndSymbolName(symbol);
		}
	}

	private Symbol createSymbol(Node child)
	{
		String type = child.toString();
		String name = null;
		ArrayList<Integer> values = null;
		switch (type)
		{
			case "FUNCTION":
				name = ((ASTFUNCTION) child).id;
				break;
			case "DECLARATION":
				Node node = child.jjtGetChild(0);
				if(node instanceof ASTSCALARELEMENT)
				{
					ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)node;
					values = getValuesFromScalarElementDeclarationIfExists(astscalarelement);
					name = astscalarelement.id;
				}
				else
				{
					ASTARRAYELEMENT astscalarelement = (ASTARRAYELEMENT)node;
					values = getValuesFromArrayElementDeclarationIfExists(astscalarelement);
					name = astscalarelement.id;
				}
				break;
			default:
				//TODO

		}

		System.out.println("symbol name: " + name);
		System.out.println("symbol type: " + type);
		System.out.println("values: ");
		for(int i = 0; i < values.size(); i++)
		{
			System.out.println(values.get(i) + " ");
		}

		return new Symbol(name, type, values);
	}

	private ArrayList<Integer> getValuesFromScalarElementDeclarationIfExists(ASTSCALARELEMENT astscalarelement)
	{
		ArrayList<Integer> values = new ArrayList<>();
		String value = astscalarelement.jjtGetValue();
		if(value != "")
			values.add(Integer.parseInt(value));
		return values;
	}

	private ArrayList<Integer> getValuesFromArrayElementDeclarationIfExists(ASTARRAYELEMENT astarrayelement)
	{
		String value = astarrayelement.jjtGetValue();
		if(value != "")
		{
			Integer arraySize = Integer.parseInt(value);
			return new ArrayList<>(arraySize);
		}

		return null;
	}

	private FileInputStream getFileStream()
	{
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(inputFile);
		} 
		catch (FileNotFoundException e)
		{
			System.out.println("Error: file " + inputFile + " not found.\n");
			System.exit(-1);
		}
		return inputStream;
	}

	private SimpleNode createAst(FileInputStream inputStream)
	{
		YalParser parser = new YalParser(inputStream);
	    SimpleNode root = null;
		try
		{
			root = parser.Module();
		} 
		catch (ParseException e)
		{
			System.out.println("Error: fatal error during parsing stage\n");
			System.exit(-1);
		}
		return root;
	}
}
