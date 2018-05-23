package yal2jvm.hhir;

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
        this.nodeType = "Allocate";
        this.name = name;
        if(value.getValue() != null)
            this.rhs = new IRConstant(value.getValue().toString());
        else
            this.rhs = new IRLoad(value);
    }

    //a = [5];
    public IRAllocate(String name, Variable value, Type arraySize)
    {
        assert arraySize == Type.ARRAYSIZE;

        this.nodeType = "Allocate";
        this.name = name;
        this.type = Type.ARRAYSIZE;
        if(value.getValue() != null)
            this.rhs = new IRConstant(value.getValue().toString());
        else
            this.rhs = new IRLoad(value);
    }

    //a[i] = 5;
    IRAllocate(VariableArray name, Variable value)
    {
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.lhsIndex = new IRLoad(name.getAt());
        if(value.getValue() != null)
            this.rhs = new IRConstant(value.getValue().toString());
        else
            this.rhs = new IRLoad(value);
    }

    //a = b[5];
    IRAllocate(Variable name, VariableArray value)
    {
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.rhs = new IRLoad(value);
    }

    //a[i] = b[5];
    IRAllocate(VariableArray name, VariableArray value)
    {
        this.nodeType = "Allocate";
        this.name = name.getVar();
        this.lhsIndex = new IRLoad(name.getAt());
        this.rhs = new IRLoad(value);
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

        //TODO VER SE REALMETE INICILAIZA A 0 SOZINHO
        if(type == Type.ARRAYSIZE)
        {
            inst.addAll(rhs.getInstructions());
            inst.add("newarray int");
        }
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

    	inst.addAll(getStoreInst());

        return inst;
    }

	private ArrayList<String> getStoreInst()
	{
        ArrayList<String> inst = new ArrayList<>();
		if (this.storeVarGlobal)
		{
			IRModule module = (IRModule)findParent("Module");
			String varType = type == Type.INTEGER ? "I" : "A";
			inst.add("putstatic " + module.getName() + "/" + name + " " + varType);
		}
		else
		{
            String varType = getVarIfExists(name).nodeType;
            if(varType != null && varType.equals(Type.INTEGER.name()))
                inst.add("istore " + this.register); //TODO _ para as primeirras 3

            if(varType == null)
                varType = rhs.nodeType;

            if(varType.equals(Type.INTEGER.name()))
                inst.addAll(setAllArrayElements());
            else
                inst.add("astore " + this.register); //TODO set como o registo que vem de IRLoad
		}

        return inst;
	}

    private ArrayList<String> setAllArrayElements()
    {
        ArrayList<String> inst = new ArrayList<>();
        IRAllocate irAllocate = getVarIfExists(name);
        if(irAllocate !=  null) // TODO ver o que fazer aqui, usar um for em jvm??
        {
            System.out.println("Internal error! The program will be closed.");
            System.exit(-1);
        }


        return inst;
    }

    private boolean storeVarIsGlobal()
	{
		IRModule module = (IRModule)findParent("Module");
		return module.getGlobal(name) != null;
	}

	private IRAllocate getVarIfExists(String varName)
	{
		IRMethod method = (IRMethod)this.parent;
		ArrayList<IRNode> children = method.getChildren();
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).toString().equals("Allocate"))
			{
				IRAllocate alloc = (IRAllocate)children.get(i);
				if (alloc.getName().equals(varName) && alloc.getRegister() != -1)
					return alloc;
			}
		}

		return null;
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
