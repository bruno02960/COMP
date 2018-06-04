package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRLoad extends IRNode
{
    private String name;
    private int register = -1;
    private Type type;
    private IRNode index = null;
    private boolean arraySizeAccess;
    private String loadedConstantValue = null;

    /**
     *
     * @param name
     */
    private IRLoad(String name)
    {
        this.name = name;
        this.setNodeType("Load");
    }

    /**
     *
     * @param name
     * @param type
     */
    public IRLoad(String name, Type type)
    {
        this(name);
        this.type = type;
    }

    /**
     *
     * @param value
     */
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

    /**
     *
     * @param value
     */
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

    /**
     *
     * @return
     */
    public String getLoadedConstantValue()
    {
        return loadedConstantValue;
    }

    /**
     *
     * @return
     */
    public int getRegister()
    {
        return register;
    }

    /**
     *
     * @param register
     */
    public void setRegister(int register)
    {
        this.register = register;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param method
     * @return
     */
    private ArrayList<String> getLocalVariableInstructions(IRMethod method)
    {
        ArrayList<String> inst = new ArrayList<>();
        int register = method.getArgumentRegister(name);
        if (register == -1)
        {
            IRAllocate var = method.getVarDeclaredUntilThis(name, this);

            //if var is const at this moment, we can put just its value and not load it from register
            ArrayList<String> constantInstructions = getConstantCodeIfConstant(method);
            if(constantInstructions != null)
                return constantInstructions;

            register = var.getRegister();
        }
        if (register > -1)
        {
            if(type == Type.INTEGER)
            {
                //if var is const at this moment, we can put just its value and not load it from register
                ArrayList<String> constantInstructions = getConstantCodeIfConstant(method);
                if(constantInstructions != null)
                    return constantInstructions;
                inst.add(getInstructionToLoadIntFromRegisterToStack(register));
            }
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

    /**
     *
     * @param method
     * @return
     */
    private ArrayList<String> getConstantCodeIfConstant(IRMethod method)
    {
        IRConstant constValue = method.getConstValueByConstVarName(name);
        if(constValue != null)
        {
            loadedConstantValue = constValue.getValue();
            return constValue.getInstructions(); //constant instructions
        }

        return null;
    }

    /**
     *
     * @param method
     * @return
     */
    private ArrayList<String> getGlobalVariableInstructions(IRMethod method)
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add(getGlobalVariableGetCodeByIRMethod(name, method));
        if(type == Type.INTEGER)
        {
            //if var is const at this moment, we can put just its value and not load it
            ArrayList<String> constantInstructions = getConstantCodeIfConstant(method);
            if(constantInstructions != null)
                return constantInstructions;
            else
                return inst;
        }
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

    /**
     *
     * @return
     */
    public Type getType()
	{
		return type;
	}

    /**
     *
     * @param type
     */
	public void setType(Type type)
	{
		this.type = type;
	}

    /**
     *
     * @return
     */
	public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public boolean isArraySizeAccess()
    {
        return arraySizeAccess;
    }

}
