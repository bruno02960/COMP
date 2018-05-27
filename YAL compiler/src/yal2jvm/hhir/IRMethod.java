package yal2jvm.hhir;

import java.util.ArrayList;

public class IRMethod extends IRNode
{

    private String name;
    private Type returnType;
    private String returnVar;
    private Type[] argsType;
    private String[] argsNames;

    private int regN;

    public IRMethod(String name, Type returnType, String returnVar, Type[] argsTypes, String[] argsNames)
    {
        this.name = name;
        this.returnType = returnType;
        this.returnVar = returnVar;
        this.argsType = argsTypes == null ? this.argsType = new Type[0] : argsTypes;
        this.argsNames = argsNames == null ? this.argsNames = new String[0] : argsNames;
        this.nodeType = "Method";
        this.regN = this.argsNames.length;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        StringBuilder methodDeclarationInst = new StringBuilder(".method public static ");

        if (name.equals("main"))
        {
            methodDeclarationInst.append("main([Ljava/lang/String;)V");
            this.regN++; // the main as the argument String args[], however is it not used in yal
        }
        else
        {
            methodDeclarationInst.append(name).append("(");
            for (Type anArgsType : argsType) {
                switch (anArgsType) {
                    case INTEGER: {
                        methodDeclarationInst.append("I");
                        break;
                    }
                    case ARRAY: {
                        methodDeclarationInst.append("[I");
                        break;
                    }
                    default:
                        break;
                }
            }
            methodDeclarationInst.append(")");

            switch (returnType)
            {
                case INTEGER:
                    methodDeclarationInst.append("I");
                    break;

                case ARRAY:
                    methodDeclarationInst.append("[I");
                    break;

                case VOID:
                    methodDeclarationInst.append("V");
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

        inst.add(methodDeclarationInst.toString());
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
        for (int i = 0; i < argsNames.length; i++)
        {
            if (argsNames[i].equals(name))
                return i;
        }
        return -1;
    }

    public Type getArgumentType(String name)
    {
        for (int i = 0; i < argsType.length; i++)
        {
            if (argsNames[i].equals(name))
                return argsType[i];
        }
        return null;
    }

    public int getVarRegister(String name)
    {
        for (IRNode aChildren : children) {
            String childrenType = aChildren.toString();
            if (childrenType.equals("Allocate")) {
                IRAllocate irAllocate = ((IRAllocate) aChildren);
                if (irAllocate.getName().equals(name))
                    return irAllocate.getRegister();
            }
        }

        return -1;
    }

    public Type getVarType(String name)
    {
        for (IRNode aChildren : children) {
            String childrenType = aChildren.toString();
            if (childrenType.equals("Allocate")) {
                IRAllocate irAllocate = ((IRAllocate) aChildren);
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

}
