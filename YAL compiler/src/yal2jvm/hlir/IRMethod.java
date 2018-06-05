package yal2jvm.hlir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class IRMethod extends IRNode
{
    private static final Map<String, Integer> instructionToStackCountValue = new HashMap<>();
    static
    {
        instructionToStackCountValue.put("getstatic", 1);
        instructionToStackCountValue.put("iload", 1);
        instructionToStackCountValue.put("aconst", 1);
        instructionToStackCountValue.put("iconst", 1);
        instructionToStackCountValue.put("dup", 1);
        instructionToStackCountValue.put("aload", 1);
        instructionToStackCountValue.put("ldc", 1);
        instructionToStackCountValue.put("bipush", 1);
        instructionToStackCountValue.put("sipush", 1);
        instructionToStackCountValue.put("istore", -1);
        instructionToStackCountValue.put("iaload", -1);
        instructionToStackCountValue.put("pop", -1);
        instructionToStackCountValue.put("iastore", -3);
        instructionToStackCountValue.put("astore", -1);
        instructionToStackCountValue.put("iadd", -1);
        instructionToStackCountValue.put("isub", -1);
        instructionToStackCountValue.put("idiv", -1);
        instructionToStackCountValue.put("imul", -1);
        instructionToStackCountValue.put("ishr", -1);
        instructionToStackCountValue.put("ishl", -1);
        instructionToStackCountValue.put("iushl", -1);
        instructionToStackCountValue.put("iand", -1);
        instructionToStackCountValue.put("ior", -1);
        instructionToStackCountValue.put("lxor", -1);
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
    private Variable[] args;
    private HashMap<String, IRConstant> constVarNameToConstValue = new HashMap<>();
    private ArrayList<HashMap<String, IRConstant>> listConstVarNameToConstValueWhileOrIfInitState = new ArrayList<>();
    private boolean insideWhile = false;
    private int regN;
	private int registerCount;

    /**
     *
     * @param name
     * @param returnType
     * @param args
     */
    public IRMethod(String name, Type returnType, Variable[] args)
    {
        this.name = name;
        this.returnType = returnType;
        this.args = args == null ? this.args = new Variable[0] : args;
        this.setNodeType("Method");
        this.regN = this.args.length;
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String methodDeclarationInst = getMethodDeclarationInstructions();

        ArrayList<String> methodBody = getMethodBody();

        inst.add(methodDeclarationInst);
        inst.addAll(methodBody);
        inst.add(".end method");
        return inst;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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
            if(node instanceof IRLabel || node instanceof IRComparison)
                handleWhileOrIfConstantPropagationOptimization(node);
        }

        inst.add(".limit locals " + registerCount);

        int stackValue = stackValueCount(childsInstructions);
        inst.add(".limit stack " + stackValue);

        inst.addAll(childsInstructions);
        return inst;
    }

    /**
     *
     * @param irNode
     */
    private void handleWhileOrIfConstantPropagationOptimization(IRNode irNode)
    {
        String label;
        if(irNode instanceof IRLabel)
            label = ((IRLabel)irNode).getLabel();
        else
            label = ((IRComparison)irNode).getLabel();

        if(irNode instanceof IRLabel && label.contains("if_false")) // for cases when we have an init false body label that indicates the a previously true body was made and is changes need to be undone
        {
            removeEntryFromConstVarNameAndSetAsBefore();
            listConstVarNameToConstValueWhileOrIfInitState.add(new HashMap<>(constVarNameToConstValue));
        }
        else if((label.contains("while_init") && irNode instanceof IRLabel) ||
                ((label.contains("if_end") || label.contains("if_false")) && irNode instanceof IRComparison))  //init label, can be: while_init ou if_... if_false ou if_... if_end ou if_falseN:
        {
            //store curr for the end
            listConstVarNameToConstValueWhileOrIfInitState.add(new HashMap<>(constVarNameToConstValue));

            //TODO REMOVE
           /* //set curr as the previous if existent, if more than one if or while stacked
            if(listConstVarNameToConstValueWhileOrIfInitState.size() > 1)
            {
                constVarNameToConstValue = listConstVarNameToConstValueWhileOrIfInitState.get(
                        listConstVarNameToConstValueWhileOrIfInitState.size() - 1);
            }*/

            if((label.contains("while_init") && irNode instanceof IRLabel))
                insideWhile = true;
        }
        else if(label.contains("_end") && irNode instanceof IRLabel)//end label
        {
            removeEntryFromConstVarNameAndSetAsBefore();
            if(label.contains("while_end"))
                insideWhile = false;
        }

    }

    private void removeEntryFromConstVarNameAndSetAsBefore()
    {
        //remove list entry
        HashMap<String, IRConstant> oldHashMap = listConstVarNameToConstValueWhileOrIfInitState.remove(
                listConstVarNameToConstValueWhileOrIfInitState.size() - 1);

        //remove constant altered on the way
        Iterator it = oldHashMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String key = (String) pair.getKey();
            String value = ((IRConstant) pair.getValue()).getValue();
            if(constVarNameToConstValue.get(key) != null)
            {
                if(constVarNameToConstValue.get(key).getValue().equals(value) == false) //remove if exists yet but the value is diferent, altered inside if or while
                    it.remove();
            }
            else
                it.remove(); //remove if the entry does not exist anymore, variable is now not constant
        }

        //set the constVarNameToConstValue as the old except the defined in the interval
        constVarNameToConstValue = oldHashMap;
    }

    /**
     *
     * @return
     */
    public HashMap<String, IRConstant> getConstVarNameToConstValue()
    {
        return constVarNameToConstValue;
    }

    /**
     *
     * @param inst
     * @return
     */
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

    /**
     *
     * @param currInstruction
     * @return
     */
    private static Integer getInstructionStackValue(String currInstruction)
    {
        //invoke has a more difficult behaviour
        if(currInstruction.contains("invokestatic"))
            return getInvokeStaticStackValue(currInstruction);

        int spaceIndex = currInstruction.indexOf(' ');
        if(spaceIndex != -1)
            currInstruction = currInstruction.substring(0, spaceIndex);
        else
        {
            int underscoreIndex = currInstruction.indexOf('_');
            if(underscoreIndex != -1)
                currInstruction = currInstruction.substring(0, underscoreIndex);
        }

        Integer instructionStackValue = instructionToStackCountValue.get(currInstruction);
        if(instructionStackValue == null)  // if not detected instruction, is an instruction that not alter stack size
            return 0;


        return instructionStackValue;
    }

    /**
     *
     * @param currInstruction
     * @return
     */
    private static Integer getInvokeStaticStackValue(String currInstruction)
    {
        //must return the number of parameters, minus one if not return void
        int numberOfParameters = 0;
        String parameters = currInstruction.substring(currInstruction.indexOf('(') + 1, currInstruction.indexOf(')'));
        if(parameters.length() != 0) //not empty string, at least one parameter
            numberOfParameters = parameters.split(",").length; // to pop from stack

        char lastCharacter = currInstruction.charAt(currInstruction.length() - 1); // to push to stack
        if(lastCharacter != 'V')
            return -(numberOfParameters - 1);
        else
            return -numberOfParameters;
    }

    /**
     *
     * @return
     */
    public int getRegN()
    {
        return regN;
    }

    /**
     *
     */
    public void incrementRegN() { this.regN++; }

    /**
     *
     * @param constVarName
     * @return
     */
    public IRConstant getConstValueByConstVarName(String constVarName)
    {
        if(insideWhile || HLIR.optimize == false)
            return null;

        return constVarNameToConstValue.get(constVarName);
    }

    /**
     *
     * @param constVarName
     * @param constValue
     */
    public void addToConstVarNameToConstValue(String constVarName, IRConstant constValue)
    {
        this.constVarNameToConstValue.put(constVarName, constValue);
    }

    public void removeFromConstVarNameToConstValue(String constVarName)
    {
        this.constVarNameToConstValue.remove(constVarName);
    }

    /**
     *
     * @param name
     * @return
     */
    public int getArgumentRegister(String name)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].getVar().equals(name))
                return i;
        }
        return -1;
    }

    /**
     *
     * @param name
     * @return
     */
    public Type getArgumentType(String name)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].getVar().equals(name))
                return args[i].getType();
        }
        return null;
    }

    /**
     *
     * @param name
     * @return
     */
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

    /**
     *
     * @param name
     * @param callerNodeThis
     * @return
     */
    public IRAllocate getVarDeclaredUntilThis(String name, IRNode callerNodeThis)
    {
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

    /**
     *
     * @param name
     * @return
     */
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

    /**
     *
     * @return
     */
	public String getName()
	{
		return name;
	}

    /**
     *
     *
     * @param name
     */
	public void setName(String name)
	{
		this.name = name;
	}

    /**
     *
     * @return
     */
	public Type getReturnType()
	{
		return returnType;
	}

    /**
     *
     * @param returnType
     */
	public void setReturnType(Type returnType)
	{
		this.returnType = returnType;
	}

    /**
     *
     * @param child
     * @param newChild
     */
    public void addNewChildAfterChild(IRNode child, IRNode newChild)
    {
        int myIndex = children.indexOf(child);
        children.add(myIndex + 1, newChild);
        newChild.setParent(this);
    }

    /**
     *
     * @return
     */
	public Variable[] getArgs()
	{
		if (this.name.equals("main") && this.args.length == 0)
		{
			Variable[] ret = new Variable[1];
			ret[0] = new Variable("mainArgs", Type.ARRAY);
			return ret;
		}
		return args;
	}

    /**
     *
     * @param args
     */
	public void setArgs(Variable[] args)
	{
		this.args = args;
	}

    /**
     *
     * @param var
     * @param register
     */
	public void assignNewRegister(String var, Integer register)
	{
		for (IRNode node : this.children)
		{
			if (node.getNodeType().equals("Allocate"))
			{
				IRAllocate alloc = (IRAllocate)node;
				if (alloc.getName().equals(var))
					alloc.setRegister(register);
			}
		}
	}

	public int getRegisterCount()
	{
		return registerCount;
	}

	public void setRegisterCount(int registerCount)
	{
		this.registerCount = registerCount;
	}
}
