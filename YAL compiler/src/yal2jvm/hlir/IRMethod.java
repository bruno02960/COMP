package yal2jvm.hlir;

import java.util.ArrayList;

public class IRMethod extends IRNode
{

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

        int localsCount = 0;
        for (int i = 0; i < getChildren().size(); i++)
        {
            IRNode node = getChildren().get(i);
            if (node.toString().equals("Allocate"))
                localsCount++;
        }
        localsCount += this.args.length;

        localsCount = 255;
        inst.add(".limit locals " + localsCount);
        inst.add(".limit stack 20");

        int numChilds = getChildren().size();
        for (int i = 0; i < numChilds; i++)
        {
            IRNode node = getChildren().get(i);
            inst.addAll(node.getInstructions());
        }
        return inst;
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
