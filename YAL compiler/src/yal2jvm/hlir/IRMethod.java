package yal2jvm.hlir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IRMethod extends IRNode
{
    private static final Map<String, Integer> intructionToStackCountValue = new HashMap<>();
    static
    {
        intructionToStackCountValue.put("getstatic", 1);
        intructionToStackCountValue.put("iload", 1);
        intructionToStackCountValue.put("iconst", 1);
        intructionToStackCountValue.put("dup", 1);
        intructionToStackCountValue.put("aload", 1);
        intructionToStackCountValue.put("ldc", 1);
        intructionToStackCountValue.put("bipush", 1);
        intructionToStackCountValue.put("istore", -1);
        intructionToStackCountValue.put("iaload", -1);
        intructionToStackCountValue.put("pop", -1);
        intructionToStackCountValue.put("astore", -1);
        intructionToStackCountValue.put("iastore", -3);
        intructionToStackCountValue.put("newarray", -1);
        intructionToStackCountValue.put("iadd", -1);
        intructionToStackCountValue.put("isub", -1);
        intructionToStackCountValue.put("idiv", -1);
        intructionToStackCountValue.put("imul", -1);
        intructionToStackCountValue.put("putstatic", -1);
    }

    private String name;
    private Type returnType;
    private String returnVar;
    private Variable[] args;

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

        ArrayList<String> methodBody = getMethodBody();

        //parse return
        IRReturn irReturn = new IRReturn(returnVar, returnType);
        this.addChild(irReturn);
        ArrayList<String> instReturn = irReturn.getInstructions();

        String endMethodInst = ".end method";

        inst.add(methodDeclarationInst);
        inst.addAll(methodBody);
        inst.addAll(instReturn);
        inst.add(endMethodInst);
        return inst;
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
        inst.add(".limit locals " + localsCount); //TODO ver o que contar aqui...se o melhor possivel ou o efetivo

        int stackValue = stackValueCount(childsInstructions);
        inst.add(".limit stack " + stackValue);

        inst.addAll(childsInstructions);
        return inst;
    }

    private int stackValueCount(ArrayList<String> inst)
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

    private Integer getInstructionStackValue(String currIntruction)
    {
        //invoke has a more difficult behaviour
        if(currIntruction.contains("invokestatic"))
            return getInvokeStaticStackValue(currIntruction);

        Iterator it = intructionToStackCountValue.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String instructionName = (String) pair.getKey();
            if(currIntruction.contains(instructionName))
            {
                Integer instructionStackValue = (Integer) pair.getValue();
                return instructionStackValue;
            }
        }

        // if not detected instruction, is an instruction that not alter stack size
        return 0;
    }

    private Integer getInvokeStaticStackValue(String currIntruction)
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

}
