package yal2jvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import yal2jvm.ast.*;
import yal2jvm.hlir.HLIR;
import yal2jvm.semantic_analysis.ModuleAnalysis;
import yal2jvm.utils.Utils;

/**
 * Yal2jvm class that reads the console's arguments and calls the project's main functions
 */
public class Yal2jvm
{
    private static final int MAX_LOCAL_VARS = 255;
    public static boolean VERBOSE = false;

    public static String moduleName;
    private int localVars;
    private boolean optimize;
    private boolean keepJFile;
    private String inputFile;
    private SimpleNode ast;

    /**
     * Constructor for the class Yal2jvm
     *
     * @param localVars
     * @param optimize
     * @param keepJFile
     * @param verbose
     * @param inputFile
     */
    public Yal2jvm(int localVars, boolean optimize, boolean keepJFile, boolean verbose, String inputFile)
    {
        this.localVars = localVars;
        this.optimize = optimize;
        this.keepJFile = keepJFile;
        Yal2jvm.VERBOSE = verbose;
        this.inputFile = inputFile;
    }

    /**
     * Reads the console's arguments and the flags set, keeping this values for the constructor
     * @param args  list of arguments
     */
    public static void main(String args[])
    {
        String inputFile = null;
        boolean optimize = false;
        boolean keepJFile = false;
        boolean verbose = false;
        int localVars = MAX_LOCAL_VARS;
        boolean validInput = true;

        if(args.length == 0)
            validInput = false;

        if(Utils.stringArrayContains(args, "-o") != -1)
            optimize = true;

        if(Utils.stringArrayContains(args, "-S") != -1)
            keepJFile = true;
        
        if(Utils.stringArrayContains(args, "-v") != -1)
            verbose = true;

        String regexForNumberBetween0And255 = "\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b";
        int registersValueIndex = Utils.stringArrayMatches(args, "-r=" + regexForNumberBetween0And255);
        if(registersValueIndex != -1)
        {
            String localVarsString = args[registersValueIndex].split("=")[1];
            localVars = Integer.parseInt(localVarsString);
        }

        String regexForFlag = "-r=" + regexForNumberBetween0And255 + "|-o|-S|-v";
        int inputFileIndex;
        if((inputFileIndex = Utils.stringArrayNotMatches(args, regexForFlag)) != -1)
            inputFile = args[inputFileIndex];

        if (inputFile != null && validInput)
            validInput = inputFile.endsWith(".yal");

        if (!validInput || inputFile == null)
        {
        	printUsage();
            System.exit(-5);
        }
        else
        {
            Yal2jvm instance = new Yal2jvm(localVars, optimize, keepJFile, verbose, inputFile);
            instance.run();
        }
    }

    /**
     *
     *
     */
    public void run()
    {
        FileInputStream inputStream = getFileStream();

        log("Initiating syntatic analysis");
        ast = createAst(inputStream);
        
        log("Completed syntatic analysis");
        if (ast == null)
            System.exit(-2);
        
        log("Printing abstract syntatic tree");
        if (VERBOSE)
        	ast.dump("");

        log("Initiating semantic analysis");
        ModuleAnalysis moduleAnalysis = new ModuleAnalysis(ast);
        moduleAnalysis.parse();
        if (ModuleAnalysis.hasErrors)
            System.exit(-3);
        log("Completed semantic analysis");
        
        log("Initiating JVM code generation");
        HLIR hlir = new HLIR(ast);
        if (VERBOSE)
        	hlir.dumpIR();
        
        if (this.optimize)
            hlir.setOptimize();

        hlir.dataflowAnalysis();
        boolean allocated = hlir.allocateRegisters(this.localVars);
        if (!allocated)
        	System.exit(-6);

        ArrayList<String> instructions = hlir.selectInstructions();
        String moduleName = hlir.getModuleName();

        log("JVM code generation completed");
        saveToJasminFile(instructions, moduleName);
        compileToBytecode(moduleName + ".j");
        log("Bytecode generated");
    }
    
    /**
     * 
     */
    private static void printUsage()
    {
        System.out.println("Insufficient or incorrect arguments for the Yal2jvm compiler");
        System.out.println("\nUsage:\tjava -jar Yal2jvm [-r=<0..255>] [-o] [-S] [-v] <input_file.yal>\n");
        System.out.println("\t-r=<0..255>       number of JVM local vars per function (default 255)  (optional)");
        System.out.println("\t-o                run three additional code optimizations              (optional)");
        System.out.println("\t-S                keep the intermediate Jasmin file (.j) on the CWD    (optional)");
        System.out.println("\t-v                allow verbose output of all compilation stages       (optional)");
        System.out.println("\t<input_file>.yal  path to the .yal file to compile.                    (mandatory)");
    }

    /**
     *
     * @return
     */
    private FileInputStream getFileStream()
    {
        FileInputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e)
        {
            System.out.println("Error: file " + inputFile + " not found.\n");
            System.exit(-4);
        }
        return inputStream;
    }

    /**
     *
     * @param inputStream
     * @return
     */
    private SimpleNode createAst(FileInputStream inputStream)
    {
        new YalParser(inputStream);
        SimpleNode root = null;
        try
        {
            root = YalParser.Module();

            int noErrors = YalParser.errorCounter.getNoErrors();
            if (noErrors > 0)
            {
                if (noErrors >= 10)
                    System.err.println("At least 10 errors found!");
                else
                    System.err.println(noErrors + " errors found!");

                return null;
            }
        } catch (ParseException e)
        {
            System.out.println("Error: fatal error during parsing stage\n");
            System.exit(-2);
        }

        return root;
    }

    /**
     *
     * @param instructions
     * @param moduleName
     */
    private void saveToJasminFile(ArrayList<String> instructions, String moduleName)
    {
        try
        {
            BufferedWriter file = new BufferedWriter(new FileWriter(moduleName + ".j"));

            for (int i = 0; i < instructions.size(); i++)
            {
                file.write(instructions.get(i));
                file.write("\n");
            }

            file.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-4);
        }
    }

    /**
     *
     * @param fileName
     */
    private void compileToBytecode(String fileName)
    {
    	Jasmin.main(new String[] {fileName});
    	
        if(!keepJFile)
        {
            File file = new File(fileName);
            file.delete();
        }
    }

    /**
     *
     * @param msg
     */
    public static void log(String msg)
    {
    	if (Yal2jvm.VERBOSE)
    		System.out.println(msg);
    }
}
