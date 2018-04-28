package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRMethod extends IRNode
{

    private String name;
    private Type returnType;
    private String returnVar;
    private Type[] argsType;
    private String[] argsNames;

    public int labelN = 0;
    private int regN = 0;
    public int varN = 0;

    public IRMethod(String name, Type returnType, String returnVar, Type[] argsTypes, String[] argsNames)
    {
        this.name = name;
        this.returnType = returnType;
        this.returnVar = returnVar;
        this.argsType = argsTypes == null ? this.argsType = new Type[0] : argsTypes;
        this.argsNames = argsNames == null ? this.argsNames = new String[0] : argsNames;
        this.nodeType = "Method";
        this.regN += this.argsNames.length + (returnVar == null ? 0 : 1);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String inst1 = ".method public static ";

        if (name.equals("main"))
            inst1 += "main([Ljava/lang/String;)V";
        else
        {
            inst1 += name + "(";
            for (int i = 0; i < argsType.length; i++)
            {
                switch (argsType[i])
                {
                    case INTEGER:
                    {
                        inst1 += "I";
                        break;
                    }
                    case ARRAY:
                        break;
                    default:
                        break;
                }
            }
            inst1 += ")";

            switch (returnType)
            {
                case INTEGER:
                {
                    inst1 += "I";
                    break;
                }
                case ARRAY:
                    break;
                case VOID:
                    inst1 += "V";
                    break;
                default:
                    break;
            }
        }

        ArrayList<String> methodBody = getMethodBody();
        ArrayList<String> instReturn = getReturn();
        String instFinal = ".end method";

        inst.add(inst1);
        inst.addAll(methodBody);
        inst.addAll(instReturn);
        inst.add(instFinal);
        return inst;
    }

    private ArrayList<String> getReturn()
    {
        ArrayList<String> inst = new ArrayList<>();
        switch (this.returnType)
        {
            case INTEGER:
            {
                inst.add("iload " + this.argsNames.length + 1);
                break;
            }
            case ARRAY:
                break;
            case VOID:
                break;
        }
        inst.add("return");
        return inst;
    }

    private ArrayList<String> getMethodBody()
    {
        ArrayList<String> inst = new ArrayList<>();

        int localsCount = 0;
        for (int i = 0; i < getChildren().size(); i++)
        {
            IRNode node = getChildren().get(i);
            if (node.toString() == "Allocate")
                localsCount++;
        }
        localsCount += this.argsType.length;

        localsCount = 255;

        inst.add(".limit locals " + localsCount);

        //if (getChildren().size() > 1)
        inst.add(".limit stack 20");

        for (int i = 0; i < getChildren().size(); i++)
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

    public void incrementRegN()
    {
        this.regN++;
    }

    public int getArgumentRegister(String name)
    {
        for (int i = 0; i < argsNames.length; i++)
        {
            if (argsNames[i].equals(name))
                return i;
        }
        return -1;
    }

    public int getVarRegister(String name)
    {
        for (int i = 0; i < children.size(); i++)
        {
            if (children.get(i).toString().equals("Allocate"))
            {
                if (((IRAllocate) children.get(i)).getName().equals(name))
                    return ((IRAllocate) children.get(i)).getRegister();
            }
        }
        return -1;
    }
}
