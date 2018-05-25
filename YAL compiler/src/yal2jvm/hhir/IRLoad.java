package yal2jvm.hhir;

import java.util.ArrayList;

public class IRLoad extends IRNode
{
    private String name;
    private int register = -1;
    private Type type;
    private IRLoad index = null;
    private boolean arraySizeAccess;

    public IRLoad(String name)
    {
        this.name = name;
        this.nodeType = "Load";
    }

    public IRLoad(Variable value)
    {
        this(value.getVar());
        this.type = Type.INTEGER; //assumes tpe is integer and changes if needed
        if(value.isSizeAccess())
        {
            arraySizeAccess = true;
            this.type = Type.ARRAY;
        }
    }

    public IRLoad(VariableArray value)
    {
        this(value.getVar());
        this.type = value.getType();
        index = new IRLoad(value.getAt());

        this.addChild(index);
    }

    public int getRegister()
    {
        return register;
    }

    public void setRegister(int register)
    {
        this.register = register;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        IRMethod method = (IRMethod) findParent("Method");
        int register = method.getVarRegister(name);
        if (register == -1)
            register = method.getArgumentRegister(name);
        if (register > -1)	//variable is local
        {
            inst.add(getInstructionToLoadIntFromRegisterToStack(register));
            if(arraySizeAccess)
                inst.add("arraylength");
            else if(index != null)
                inst.addAll(index.getInstructions());
        }
        else  //variable is global
        {
            IRModule module = ((IRModule) method.getParent());
            IRGlobal global = module.getGlobal(name);
            if (global == null)
            {
                System.out.println("Internal error! The program will be closed.");
                System.exit(-1);
            }

            String in = "getstatic " + module.getName() + "/" + global.getName() + " ";
            in += global.getType() == Type.INTEGER ? "I" : "A";
            inst.add(in);
        }



        return inst;
    }

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
}
