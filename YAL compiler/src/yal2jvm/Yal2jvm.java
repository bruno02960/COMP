package yal2jvm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.temporal.ChronoUnit;

import yal2jvm.SymbolTables.Symbol;
import yal2jvm.SymbolTables.SymbolTable;
import yal2jvm.ast.ParseException;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.YalParser; 

public class Yal2jvm
{
	private int localVars;
	private boolean optimize;
	private String inputFile;
	private SimpleNode ast;
	private SymbolTable globals = new SymbolTable();

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
	}
	
	private void initiateGlobalAST()
	{
		int numChildren = ast.jjtGetNumChildren();
		
		for(int i = 0; i < numChildren; i++)
		{
			SimpleNode child = (SimpleNode) ast.jjtGetChild(i);
			Symbol symbol = new Symbol(child.jjtGetId(), child.jjtGetValue());
			globals.addSymbolAndSymbolName(symbol, child.jjtGetId());
			//add id to the ast of globals
		}
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
