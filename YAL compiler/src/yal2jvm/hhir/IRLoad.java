package yal2jvm.hhir;

import java.util.ArrayList;

public class IRLoad extends IRNode
{
    private String name;
    private Type type;
    private IRNode index = null;
    private boolean arraySizeAccess;

    private IRLoad(String name)
    {
        this.name = name;
        this.nodeType = "Load";
    }

    IRLoad(String name, Type type)
    {
        this(name);
        this.type = type;
    }

    IRLoad(Variable value)
    {
        this(value.getVar());
        this.type = Type.INTEGER; //assumes type is integer and changes if needed
        if(value.isSizeAccess())
        {
            arraySizeAccess = true;
            this.type = Type.ARRAY;
        }
    }

    IRLoad(VariableArray value)
    {
        this(value.getVar());
        this.type = value.getType();

        Variable indexVar = value.getAt();
        if(indexVar.getType() == Type.INTEGER)
            index = new IRConstant(indexVar.getVar());
        else
            index = new IRLoad(indexVar);

        this.addChild(index);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        IRMethod method = (IRMethod) findParent("Method");
        IRModule module = (IRModule) method.getParent();
        IRGlobal irGlobal = module.getGlobal(name);
        if(irGlobal != null) //variable is global
        {
            inst.add(getGlobalVariableGetCode(name, method));
            if(type == Type.INTEGER)
                return inst;
            else
            {
                if(arraySizeAccess)
                    inst.add("arraylength");
                else if(index != null)
                {
                    inst.addAll(index.getInstructions());
                    inst.add("iaload");
                }
            }
            return inst;
        }

        int register = method.getArgumentRegister(name);
        if (register == -1)
            register = method.getVarRegister(name);
        if (register > -1)	//variable is local
        {
            if(type == Type.INTEGER)
                inst.add(getInstructionToLoadIntFromRegisterToStack(register));
            else
            {
                inst.add(getInstructionToLoadArrayFromRegisterToStack(register));
                if(arraySizeAccess)
                    inst.add("arraylength");
                else if(index != null)
                {
                    inst.addAll(index.getInstructions());
                    inst.add("iaload");
                }
            }

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

	public String getName() {
        return name;
    }

    public boolean isArraySizeAccess()
    {
        return arraySizeAccess;
    }

}
