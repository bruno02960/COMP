package yal2jvm.hlir;

import java.util.ArrayList;

public class IRLoad extends IRNode
{
    private String name;
    private int register = -1;
    private Type type;
    private IRNode index = null;
    private boolean arraySizeAccess;

    private IRLoad(String name)
    {
        this.name = name;
        this.setNodeType("Load");
    }

    public IRLoad(String name, Type type)
    {
        this(name);
        this.type = type;
    }

    public IRLoad(Variable value)
    {
        this(value.getVar());
        this.type = Type.INTEGER; //assumes type is integer and changes if needed
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

        Variable indexVar = value.getAt();
        if(indexVar.getType() == Type.INTEGER)
            index = new IRConstant(indexVar.getVar());
        else
            index = new IRLoad(indexVar);

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
        IRMethod method = (IRMethod) findParent("Method");
        IRModule module = (IRModule) method.getParent();
        IRGlobal irGlobal = module.getGlobal(name);
        if(irGlobal != null)
            return getGlobalVariableInstructions(method);
        else
            return getLocalVariableInstructions(method);
    }

    private ArrayList<String> getLocalVariableInstructions(IRMethod method)
    {
        ArrayList<String> inst = new ArrayList<>();
        int register = method.getArgumentRegister(name);
        if (register == -1)
            register = method.getVarRegister(name);
        if (register > -1)
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

    private ArrayList<String> getGlobalVariableInstructions(IRMethod method)
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add(getGlobalVariableGetCodeByIRMethod(name, method));
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
