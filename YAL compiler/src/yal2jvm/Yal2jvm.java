package yal2jvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import yal2jvm.ast.*;
import yal2jvm.hhir.HHIR;
import yal2jvm.semantic_analysis.ModuleAnalysis;
import yal2jvm.utils.Utils;

public class Yal2jvm
{

    private static final int MAX_LOCAL_VARS = 255;

    private int localVars;
    private boolean optimize;
    private boolean keepJFile;
    private String inputFile;
    private SimpleNode ast;

    public Yal2jvm(int localVars, boolean optimize, boolean keepJFile, String inputFile)
    {
        this.localVars = localVars;
        this.optimize = optimize;
        this.keepJFile = keepJFile;
        this.inputFile = inputFile;
    }

    public static void main(String args[])
    {
        String inputFile = null;
        boolean optimize = false;
        boolean keepJFile = false;
        int localVars = MAX_LOCAL_VARS;
        boolean validInput = true;

        if(args.length == 0)
            validInput = false;

        if(Utils.stringArrayContains(args, "-o") != -1)
            optimize = true;

        if(Utils.stringArrayContains(args, "-S") != -1)
            keepJFile = true;

        String regexForNumberBetween0And255 = "\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b";
        if(Utils.stringArrayMatches(args, "-r=" + regexForNumberBetween0And255) != -1)
            localVars = Integer.parseInt(args[1].split("=")[1]);

        String regexForFlag = "-r=" + regexForNumberBetween0And255 + "|-o|-S";
        int inputFileIndex;
        if((inputFileIndex = Utils.stringArrayNotMatches(args, regexForFlag)) != -1)
            inputFile = args[inputFileIndex];

        if (inputFile != null && validInput)
            validInput = inputFile.endsWith(".yal");

        if (!validInput && inputFile != null)
        {
            System.out.println("Insufficient or incorrect arguments for the Yal2jvm compiler");
            System.out.println("\nUsage:\tjava Yal2jvm [-r=<0..255>] [-o] [-S] <input_file.yal>\n");
            System.exit(-5);
        }
        else
        {
            Yal2jvm instance = new Yal2jvm(localVars, optimize, keepJFile, inputFile);
            instance.run();
        }
    }

    public void run()
    {
        FileInputStream inputStream = getFileStream();

        System.out.println("Initiating syntatic analysis");
        ast = createAst(inputStream);
        System.out.println("Completed syntatic analysis");
        if (ast == null)
            System.exit(-2);
        System.out.println("Printing abstract syntatic tree");
        ast.dump("");

        System.out.println("Initiating semantic analysis");
        ModuleAnalysis moduleAnalysis = new ModuleAnalysis(ast);
        moduleAnalysis.parse();
        System.out.println("Completed semantic analysis");

        if (ModuleAnalysis.hasErrors)
            System.exit(-3);
        
        System.out.println("Initiating JVM code generation");
        HHIR hhir = new HHIR(ast);
        if (this.optimize)
            hhir.optimize();
        hhir.dataflowAnalysis();
        hhir.allocateRegisters(this.localVars);

        /*ArrayList<String> instructions = hhir.selectInstructions();
        String moduleName = hhir.getModuleName();

        System.out.println("JVM code generation completed");
        saveToJasminFile(instructions, moduleName);
        compileToBytecode(moduleName + ".j");
        System.out.println("Bytecode generated");*/
    }

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
                if (noErrors == 10)
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

    private void compileToBytecode(String fileName)
    {
        try
        {
            Runtime.getRuntime().exec("java -jar jasmin.jar " + fileName).waitFor();
            if(!keepJFile)
            {
                File file = new File(fileName);
                file.delete();
            }
        }
        catch (IOException | InterruptedException e)
        {
            System.out.println("Unable to find or execute jasmin.jar");
            System.exit(-1);
        }
    }

    public void setInputFile(String inputFile)
    {
        this.inputFile = inputFile;
    }
}
