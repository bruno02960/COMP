package yal2jvm.hlir;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{
    private String name;
    private IRNode lhsIndex = null;
    private Type type;
    private IRNode rhs;
    private int register = -1;
	private boolean storeVarGlobal = false;
	private IRGlobal global;

	//a = 1;
    public IRAllocate(String name, Variable value)
    {
        this.type = Type.INTEGER;
        this.setNodeType("Allocate");
        this.name = name;

        if(value.getType().equals(Type.INTEGER))
            this.rhs = new IRConstant(value.getVar());
        else
            this.rhs = new IRLoad(value);

        this.addChild(this.rhs);
    }

    //a = [5];
    public IRAllocate(String name, Variable value, Type arraySize)
    {
        assert arraySize == Type.ARRAYSIZE;

        this.setNodeType("Allocate");
        this.name = name;
        this.type = Type.ARRAYSIZE;

        if(value.getType().equals(Type.INTEGER))
            this.rhs = new IRConstant(value.getVar());
        else
            this.rhs = new IRLoad(value);
        this.addChild(this.rhs);
    }

    //a[i] = 5;
    IRAllocate(VariableArray name, Variable value)
    {
        this.type = Type.ARRAY;
        this.setNodeType("Allocate");
        this.name = name.getVar();

        Variable at = name.getAt();
        if(at.getType().equals(Type.INTEGER))
            this.lhsIndex = new IRConstant(at.getVar());
        else
            this.lhsIndex = new IRLoad(at);
        this.addChild(this.lhsIndex);

        if(value.getType().equals(Type.INTEGER))
            this.rhs = new IRConstant(value.getVar());
        else
            this.rhs = new IRLoad(value);
        this.addChild(this.rhs);
    }

    //a = b[5];
    IRAllocate(Variable name, VariableArray value)
    {
        this.setNodeType("Allocate");
        this.name = name.getVar();
        this.type = Type.INTEGER;

        this.rhs = new IRLoad(value);
        this.addChild(this.rhs);
    }

    //a[i] = b[5];
    IRAllocate(VariableArray name, VariableArray value)
    {
        this.setNodeType("Allocate");
        this.name = name.getVar();
        this.type = Type.ARRAY;

        Variable at = name.getAt();
        if(at.getType().equals(Type.INTEGER))
            this.lhsIndex = new IRConstant(at.getVar());
        else
            this.lhsIndex = new IRLoad(at);
        this.addChild(this.lhsIndex);

        this.rhs = new IRLoad(value);
        this.addChild(this.rhs);
    }

    public Type getType()
    {
        return type;
    }

    public IRNode getRhs()
    {
        return rhs;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        handleConstantRhsForConstantPropagationOtimization();

        IRNode node = getVarIfExists(name);
        if(node == null)
        {
            initRegister();
        }
        else if(node instanceof IRGlobal)
        {
            global = (IRGlobal) node;
            this.storeVarGlobal  = true;
        }
        else if(node instanceof IRArgument)
            register = ((IRArgument)node).getRegister();
        else
            register = ((IRAllocate) node).getRegister();

        if(type == Type.ARRAYSIZE)
        {
            inst.addAll(rhs.getInstructions());
            inst.add("newarray int");
        }

        //get store instructions
        inst.addAll(getStoreInst());

        return inst;
    }

    private void handleConstantRhsForConstantPropagationOtimization()
    {
        IRMethod method = (IRMethod) parent;
        if(rhs instanceof IRConstant)
            method.addToConstVarNameToConstValue(name, (IRConstant) rhs); //TODO ver se nao dá porblema usar o mesmo rhs, secalhar copia pode ser mehor
        else if(method.getConstValueByConstVarName(((IRLoad)rhs).getName()) != null)
        {
            rhs = new IRConstant(method.getConstValueByConstVarName(((IRLoad)rhs).getName()).getValue());
            method.addToConstVarNameToConstValue(name, (IRConstant) rhs);
        }
    }

    private ArrayList<String> getStoreInst()
	{
        ArrayList<String> inst = new ArrayList<>();
		if (this.storeVarGlobal)
		{
		    String typeStr = type.name();
            if(typeStr != null && global.getType() == Type.VARIABLE) // i = 5;
            {
                inst.addAll(rhs.getInstructions());
                inst.add(getInstructionToStoreGlobalArray(type, name));
                return inst;
            }

            if(typeStr == null)
                typeStr = rhs.getNodeType();

            if(rhs.parent.getNodeType().equals("Allocate")) {
                IRAllocate rhsParent = (IRAllocate) rhs.parent;
                if(rhsParent.type == Type.ARRAYSIZE) {
                    inst.add(getInstructionToStoreGlobalArray(type, name)); // i = [5];
                    type = Type.ARRAY;
                    return inst;
                }
            }

            Type prevType = global.getType();
            if(prevType == Type.ARRAY && type == Type.INTEGER)
                typeStr = Type.ARRAY.name();

            if(typeStr.equals(Type.ARRAY.name()) && lhsIndex == null)
                inst.addAll(setAllArrayElements()); // i = 5; com i array
            else

            {
                if(lhsIndex != null) // a[i] = 5;
                    inst.addAll(setGlobalArrayElementByIRNode(lhsIndex, type, name, rhs));
            }

		}
		else
		{
            String varType;
            IRMethod method = (IRMethod) findParent("Method");
            IRNode node = getVarIfExists(name);
            if(node instanceof IRAllocate)
                varType = ((IRAllocate)node).getType().name();
            else
                varType = method.getArgumentType(name).name();

            if(varType != null && varType.equals(Type.INTEGER.name())) // i = 5;
            {
                inst.addAll(rhs.getInstructions());
                inst.add(getInstructionToStoreIntInRegister(this.register));

                //this is done after getInstructions of rhs, because loadConstant ir set there
                if(rhs instanceof IRConstant)
                    method.addToConstVarNameToConstValue(name, (IRConstant) rhs);
                else
                {
                    String value = ((IRLoad)rhs).getLoadedConstantValue();
                    if(value != null)
                        method.addToConstVarNameToConstValue(name, new IRConstant(value));
                }

                return inst;
            }

            if(varType == null)
                varType = rhs.getNodeType();

            if(rhs.parent.getNodeType().equals("Allocate")) {
                IRAllocate rhsParent = (IRAllocate) rhs.parent;
                if(rhsParent.type == Type.ARRAYSIZE) {
                    inst.add(getInstructionToStoreArrayInRegister(this.register)); // i = [5];
                    type = Type.ARRAY;
                    return inst;
                }
            }

            if(varType.equals(Type.ARRAY.name()) && lhsIndex == null)
                inst.addAll(setAllArrayElements()); // i = 5; com i array
            else
            {
                if(lhsIndex != null) // a[i] = 5;
                    inst.addAll(setLocalArrayElementByIRNode(lhsIndex, register, rhs));
            }
		}

        return inst;
	}

    private ArrayList<String> setAllArrayElements()
    {
        int reg = -1;
        IRNode node = getVarIfExists(name);
        if(node == null)
        {
            System.out.println("Internal error! The program will be closed.");
            System.exit(-1);
        }
        else if(node instanceof IRArgument)
            reg = ((IRArgument)node).getRegister();
        else if(node instanceof IRAllocate)
            reg = ((IRAllocate) node).getRegister();

        String arrayRefJVMCode;
        if(storeVarGlobal)
        {
            Type prevType = global.getType();
            if(prevType == Type.ARRAY && type == Type.INTEGER)
                type = Type.ARRAY;
            arrayRefJVMCode = getInstructionToLoadGlobalArrayToStack(type, name);
        }
        else
            arrayRefJVMCode = getInstructionToLoadArrayFromRegisterToStack(reg);
        ArrayList<String> valueJVMCode = rhs.getInstructions();
        return getCodeForSetAllArrayElements(arrayRefJVMCode, valueJVMCode);
    }

    private IRGlobal storeVarGlobal()
	{
		IRModule module = (IRModule)findParent("Module");
		return module.getGlobal(name);
	}

    private void initRegister()
    {
        if (!this.storeVarGlobal && this.register == -1)
        {
            this.register = ((IRMethod) parent).getRegN();
            ((IRMethod) parent).incrementRegN();
        }
    }

    public int getRegister()
    {
        initRegister();
        return register;
    }

    public void setRegister(int register)
    {
    	if (!this.storeVarGlobal)
    		this.register = register;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
