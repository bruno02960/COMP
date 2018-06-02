package yal2jvm.hlir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IRMethod extends IRNode
{
    private static final Map<String, Integer> instructionToStackCountValue = new HashMap<>();
    static
    {
        instructionToStackCountValue.put("getstatic", 1);
        instructionToStackCountValue.put("iload", 1);
        instructionToStackCountValue.put("iconst", 1);
        instructionToStackCountValue.put("dup", 1);
        instructionToStackCountValue.put("aload", 1);
        instructionToStackCountValue.put("ldc", 1);
        instructionToStackCountValue.put("bipush", 1);
        instructionToStackCountValue.put("istore", -1);
        instructionToStackCountValue.put("iaload", -1);
        instructionToStackCountValue.put("pop", -1);
        instructionToStackCountValue.put("iastore", -3);
        instructionToStackCountValue.put("astore", -1);
        instructionToStackCountValue.put("iadd", -1);
        instructionToStackCountValue.put("isub", -1);
        instructionToStackCountValue.put("idiv", -1);
        instructionToStackCountValue.put("imul", -1);
        instructionToStackCountValue.put("putstatic", -1);
        instructionToStackCountValue.put("if_icmpeq", -2);
        instructionToStackCountValue.put("if_icmpgt", -2);
        instructionToStackCountValue.put("if_icmpge", -2);
        instructionToStackCountValue.put("if_icmpne", -2);
        instructionToStackCountValue.put("if_icmplt", -2);
        instructionToStackCountValue.put("if_icmple", -2);
        instructionToStackCountValue.put("if_acmpeq", -2);
        instructionToStackCountValue.put("if_acmpne", -2);
        instructionToStackCountValue.put("ifeq", -1);
        instructionToStackCountValue.put("ifgt", -1);
        instructionToStackCountValue.put("ifge", -1);
        instructionToStackCountValue.put("ifne", -1);
        instructionToStackCountValue.put("iflt", -1);
        instructionToStackCountValue.put("ifle", -1);
    }

    private String name;
    private Type returnType;
    private String returnVar;
    private Variable[] args;
    private HashMap<String, IRConstant> constVarNameToConstValue = new HashMap<>();

    private int labelN = 0;
    private int regN = 0;
    private int varN = 0;

    public IRMethod(String name, Type returnType, String returnVar, Variable[] args)
    {
        this.name = name;
        this.returnType = returnType;
        this.returnVar = returnVar;
        this.args = args == null ? this.args = new Variable[0] : args;
        this.setNodeType("Method");
        this.regN = this.args.length;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String methodDeclarationInst = getMethodDeclarationInstructions();

        //parse return
        IRReturn irReturn = new IRReturn(returnVar, returnType);
        this.addChild(irReturn);

        ArrayList<String> methodBody = getMethodBody();

        inst.add(methodDeclarationInst);
        inst.addAll(methodBody);
        inst.add(".end method");
        return inst;
    }

    private String getMethodDeclarationInstructions()
    {
        String methodDeclarationInst = ".method public static ";

        if (name.equals("main"))
        {
            methodDeclarationInst += "main([Ljava/lang/String;)V";
            this.regN++; // the main as the argument String args[], however is it not used in yal
        }
        else
        {
            methodDeclarationInst += name + "(";
            for (int i = 0; i < args.length; i++)
            {
                switch (args[i].getType())
                {
                    case INTEGER:
                    {
                        methodDeclarationInst += "I";
                        break;
                    }
                    case ARRAY:
                    {
                        methodDeclarationInst += "[I";
                        break;
                    }
                    default:
                        break;
                }
            }
            methodDeclarationInst += ")";

            switch (returnType)
            {
                case INTEGER:
                    methodDeclarationInst += "I";
                    break;

                case ARRAY:
                    methodDeclarationInst += "[I";
                    break;

                case VOID:
                    methodDeclarationInst += "V";
                    break;
                default:
                    break;
            }
        }
        return methodDeclarationInst;
    }

    private ArrayList<String> getMethodBody()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<String> childsInstructions = new ArrayList<>();
        int numChilds = getChildren().size();
        for (int i = 0; i < numChilds; i++)
        {
            IRNode node = getChildren().get(i);
            childsInstructions.addAll(node.getInstructions());
            if(getChildren().size() > numChilds)
            {
                i++;
                numChilds = getChildren().size();
            }
        }


        int localsCount = 0;
        for (int i = 0; i < getChildren().size(); i++)
        {
            IRNode node = getChildren().get(i);
            if (node.toString().equals("Allocate"))
                localsCount++;
        }
        localsCount += this.args.length;

        localsCount = 255;
        inst.add(".limit locals " + localsCount); //TODO ver o que contar aqui...a register allocaton vai dar isso

        int stackValue = stackValueCount(childsInstructions);
        inst.add(".limit stack " + stackValue);

        inst.addAll(childsInstructions);
        return inst;
    }

    public static int stackValueCount(ArrayList<String> inst)
    {
        //search in child's code for instruction that put or remove elements from the stack
        int currStackCount = 0;
        int maxStackCount = 0;
        for(int i = 0; i < inst.size(); i++)
        {
            String currInstruction = inst.get(i);
            currStackCount += getInstructionStackValue(currInstruction);
            if(currStackCount > maxStackCount)
                maxStackCount = currStackCount;
        }

        //TODO DEBUG
        System.out.println(currStackCount);
        System.out.println(maxStackCount);
        System.out.println();
        System.out.println();
        return maxStackCount;
    }

    private static Integer getInstructionStackValue(String currInstruction)
    {
        //invoke has a more difficult behaviour
        if(currInstruction.contains("invokestatic"))
            return getInvokeStaticStackValue(currInstruction);

        int underscoreIndex = currInstruction.indexOf('_');
        if(underscoreIndex != -1)
            currInstruction = currInstruction.substring(0, underscoreIndex);
        else
        {
            int spaceIndex = currInstruction.indexOf(' ');
            if(spaceIndex != -1)
                currInstruction = currInstruction.substring(0, spaceIndex);
        }

        Integer instructionStackValue = instructionToStackCountValue.get(currInstruction);
        if(instructionStackValue == null)  // if not detected instruction, is an instruction that not alter stack size
            return 0;


        return instructionStackValue;

        //TODO REMOVE PROBABLY NOT NECESSARY
       /* Iterator it = instructionToStackCountValue.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String instructionName = (String) pair.getKey();
            if(currInstruction.contains(instructionName))
            {
                Integer instructionStackValue = (Integer) pair.getValue();
                return instructionStackValue;
            }
        }

        // if not detected instruction, is an instruction that not alter stack size
        return 0;*/
    }

    private static Integer getInvokeStaticStackValue(String currIntruction)
    {
        //must return the number of parameters, minus one if not return void
        String parameters = currIntruction.substring(currIntruction.indexOf('(') + 1, currIntruction.indexOf(')'));
        int numberOfParameters = parameters.split(",").length;

        char lastCharacter = currIntruction.charAt(currIntruction.length() - 1);
        if(lastCharacter != 'V')
            return -(numberOfParameters - 1);
        else
            return -numberOfParameters;
    }

    public int getRegN()
    {
        return regN;
    }

    public void incrementRegN() { this.regN++; }

    public IRConstant getConstValueByConstVarName(String constVarName)
    {
        return constVarNameToConstValue.get(constVarName);
    }

    public void addToConstVarNameToConstValue(String constVarName, IRConstant constValue)
    {
        this.constVarNameToConstValue.put(constVarName, constValue);
    }

    public int getArgumentRegister(String name)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].getVar().equals(name))
                return i;
        }
        return -1;
    }

    public Type getArgumentType(String name)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].getVar().equals(name))
                return args[i].getType();
        }
        return null;
    }

    public int getVarRegister(String name)
    {
        //TODO SERÃ� QUE PODIA SER ATE AO THIS, OU SEJA Ã� CURR INSTRUCTION?
        // TODO ASSIM JA NAO FALHAVA O ENCONTRAR VARAIVEIS QUE SO FORAM DECLARADAS DEPOIS
        for (int i = 0; i < children.size(); i++)
        {
            String childrenType = children.get(i).toString();
            if (childrenType.equals("Allocate"))
            {
                IRAllocate irAllocate = ((IRAllocate) children.get(i));
                if (irAllocate.getName().equals(name))
                    return irAllocate.getRegister();
            }
        }

        return -1;
    }

    public IRAllocate getVarDeclaredUntilThis(String name, IRNode callerNodeThis)
    {
        //TODO SERÃ� QUE PODIA SER ATE AO THIS, OU SEJA Ã� CURR INSTRUCTION?
        // TODO ASSIM JA NAO FALHAVA O ENCONTRAR VARAIVEIS QUE SO FORAM DECLARADAS DEPOIS
        for (int i = 0; i < children.size(); i++)
        {
            IRNode currChild = children.get(i);
            if(currChild == callerNodeThis) // stop if curr method child is the caller node
                break;
            String childrenType = currChild.toString();
            if (childrenType.equals("Allocate"))
            {
                IRAllocate irAllocate = ((IRAllocate)currChild);
                if (irAllocate.getName().equals(name))
                    return irAllocate;
            }
        }

        return null;
    }

    public int getVarRegisterDeclaredUntilThis(String name, IRNode callerNodeThis)
    {
        //TODO SERÃ� QUE PODIA SER ATE AO THIS, OU SEJA Ã� CURR INSTRUCTION?
        // TODO ASSIM JA NAO FALHAVA O ENCONTRAR VARAIVEIS QUE SO FORAM DECLARADAS DEPOIS

        IRAllocate var = getVarDeclaredUntilThis(name, callerNodeThis);
        if(var == null)
            return -1;

        return var.getRegister();
    }

    public Type getVarType(String name)
    {
        for (int i = 0; i < children.size(); i++)
        {
            String childrenType = children.get(i).toString();
            if (childrenType.equals("Allocate"))
            {
                IRAllocate irAllocate = ((IRAllocate) children.get(i));
                if (irAllocate.getName().equals(name))
                    return irAllocate.getType();
            }
        }
        return null;
    }

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public void setReturnType(Type returnType)
	{
		this.returnType = returnType;
	}

    public void addNewChildAfterChild(IRNode child, IRNode newChild)
    {
        int myIndex = children.indexOf(child);
        children.add(myIndex + 1, newChild);
        newChild.setParent(this);
    }

	public Variable[] getArgs()
	{
		return args;
	}

	public void setArgs(Variable[] args)
	{
		this.args = args;
	}
}
