package yal2jvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import yal2jvm.HHIR.IRAllocate;
import yal2jvm.HHIR.IRConstant;
import yal2jvm.HHIR.IRGlobal;
import yal2jvm.HHIR.IRMethod;
import yal2jvm.HHIR.IRModule;
import yal2jvm.HHIR.IRReturn;
import yal2jvm.HHIR.IRStoreArith;
import yal2jvm.HHIR.Operation;
import yal2jvm.HHIR.Type;
import yal2jvm.SemanticAnalysis.ModuleAnalysis;
import yal2jvm.ast.*;

public class Yal2jvm
{
	private int localVars;
	private boolean optimize;
	private String inputFile;
	private SimpleNode ast;
	public static int s;

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

        ModuleAnalysis moduleAnalysis = new ModuleAnalysis(ast);
        moduleAnalysis.parse();
        //create HHIR
		//IRModule module = moduleAnalysis.parse();
        
        IRModule module = createHardcodedIR("Module1");
        ArrayList<String> instructions = module.getInstructions();
        String moduleName = module.getName();
        
        saveToJasminFile(instructions, moduleName);
        compileToBytecode(moduleName + ".j");

        System.exit(0);
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
	
	private void saveToJasminFile(ArrayList<String> instructions, String moduleName)
	{
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(moduleName + ".j"));
			
			for (int i = 0; i < instructions.size(); i++)
			{
				file.write(instructions.get(i));
				file.write("\n");
				System.out.println(instructions.get(i));
			}
			
			file.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	private void compileToBytecode(String fileName)
	{
        try
		{
			Runtime.getRuntime().exec("java -jar jasmin.jar " + fileName).waitFor();
			File file = new File(fileName);
			//file.delete();
		} 
        catch (IOException | InterruptedException e)
		{
			System.out.println("Unable to find or execute jasmin.jar");
		}
	}
	
	private IRModule createHardcodedIR(String moduleName)
	{
		IRModule module = new IRModule("Module1");
		module.addChild(new IRGlobal("a", Type.INTEGER, null));
		module.addChild(new IRGlobal("b", Type.INTEGER, null));
		module.addChild(new IRGlobal("c", Type.INTEGER, 12));
		module.addChild(new IRGlobal("d", Type.INTEGER, 12345));
		module.addChild(new IRMethod("method1", Type.INTEGER, null));
		module.addChild(new IRMethod("method2", Type.VOID, new Type[]{Type.INTEGER}));
		module.addChild(new IRMethod("method3", Type.VOID, new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}));
		
			IRMethod method = new IRMethod("main", Type.VOID, null);
			method.addChild(new IRAllocate("var1", Type.INTEGER, null));
			method.addChild(new IRAllocate("var2", Type.INTEGER, 10));
			method.addChild(new IRAllocate("var3", Type.INTEGER, 20000));
		
				IRStoreArith arith = new IRStoreArith("var1", Operation.ADD);
				arith.setRhs(new IRConstant(100));
				arith.setLhs(new IRConstant(200));
		
			method.addChild(arith);
			method.addChild(new IRReturn(null, null));
		
		module.addChild(method);
		return module;
	}
}
