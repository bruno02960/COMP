package yal2jvm.hhir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{
    private String name;
    private IRLoad lhsIndex = null;
    private Type type;
    private IRLoad rhsIndex = null;
    private IRNode rhs;
    private int register = -1;
	private boolean storeVarGlobal = false;


	//a = 1;
    public IRAllocate(String name, Variable value)
    {
        this.type = Type.INTEGER;
        this.nodeType = "Allocate";
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

        this.type = Type.ARRAY;
        this.nodeType = "Allocate";
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
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.type = Type.ARRAY;
        this.lhsIndex = new IRLoad(name.getAt());
        if(value.getType().equals(Type.INTEGER))
            this.rhs = new IRConstant(value.getVar());
        else
            this.rhs = new IRLoad(value);

        this.addChild(this.rhs);
        this.addChild(this.lhsIndex);
    }

    //a = b[5];
    IRAllocate(Variable name, VariableArray value)
    {
        this.type = Type.ARRAY;
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.type = Type.INTEGER;
        this.rhs = new IRLoad(value);

        this.addChild(this.rhs);
    }

    //a[i] = b[5];
    IRAllocate(VariableArray name, VariableArray value)
    {
        this.type = Type.ARRAY;
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.type = Type.ARRAY;
        this.lhsIndex = new IRLoad(name.getAt());
        this.rhs = new IRLoad(value);

        this.addChild(this.lhsIndex);
        this.addChild(this.rhs);
    }

    public Type getType()
    {
        return type;
    }


    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        
        if (storeVarIsGlobal())
    	{
    		this.storeVarGlobal  = true;
    	}
        else
        {
            IRAllocate irAllocate = getVarIfExists(this.name);
            if(irAllocate != null && irAllocate.register != -1)
		        this.register = irAllocate.register;
            else
		        initRegister();
        }

        if(type == Type.ARRAYSIZE)
        {
            inst.addAll(rhs.getInstructions());
            inst.add("newarray int");
            inst.addAll(getStoreInst());
        }
        else
        {
            inst.addAll(getStoreInst());
        }

        //TODO: remove if not necessary
 /*
        //assign a variable
    	if (this.variable != null)
    	{
            switch (type)
            {

        		int otherReg = getVarIfExists(this.variable);
        		if (otherReg != -1)
        		{
        			if (this.index == null && this.)
        			inst.add("iload " + otherReg);
        		}
        		else
        		{
        			IRLoad var = new IRLoad(this.variable);
        			this.addChild(var);
        			inst.addAll(var.getInstructions());
        		}
            }
    	}
    	else	//assign a constant
    	{
    		switch(type)
    		{
    			
    		}
    		inst.add(IRConstant.getLoadConstantInstruction(this.value));
    	}*/

        return inst;
    }

	private ArrayList<String> getStoreInst()
	{
        ArrayList<String> inst = new ArrayList<>();
		if (this.storeVarGlobal)
		{
			String varType = type == Type.INTEGER ? "I" : "A";
			inst.add("putstatic " + Yal2jvm.moduleName + "/" + name + " " + varType);
		}
		else
		{
            String varType = getVarIfExists(name).type.name();
            if(varType != null && varType.equals(Type.INTEGER.name())) { // i = 5;
                inst.add(getInstructionToStoreIntInRegister(this.register));
                return inst;

                //TODO: verify
            }

            if(varType == null)
                varType = rhs.nodeType;

            if(varType.equals(Type.INTEGER.name()))
                inst.addAll(setAllArrayElements()); // i = 5; com i array
            else
            {
                if(lhsIndex != null) // a[i] = 5;
                    inst.addAll(setArrayElementByIRNode(lhsIndex, register, rhs));
                else
                    inst.add(getInstructionToStoreArrayInRegister(this.register)); // i = [5];
            }
		}

        return inst;
	}

	//TODO MOVED TO IRNode, mas aquele inst.addAll(rhsIndex.getInstructions()); acho que nao faz sen
   /* private ArrayList<String> setArrayElementByIRNode()
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add(getInstructionToLoadArrayFromRegisterToStack(this.register));
        inst.addAll(lhsIndex.getInstructions());
        inst.addAll(rhsIndex.getInstructions());
        inst.add("iastore");

        return inst;
    }*/

    private ArrayList<String> setAllArrayElements()
    {
        ArrayList<String> inst = new ArrayList<>();
        IRAllocate irAllocate = getVarIfExists(name);
        if(irAllocate !=  null) // TODO ver o que fazer aqui, usar um for em jvm??
        {
            System.out.println("Internal error! The program will be closed.");
            System.exit(-1);
        }

        inst.add(getInstructionToLoadArrayFromRegisterToStack(irAllocate.getRegister()));

        return inst;
    }

    private boolean storeVarIsGlobal()
	{
		IRModule module = (IRModule)findParent("Module");
		return module.getGlobal(name) != null;
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
