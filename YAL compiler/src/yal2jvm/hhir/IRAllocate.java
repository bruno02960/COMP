package yal2jvm.hhir;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{
    private String name;
    private Type type;
    private IRNode rhs;
    private int register = -1;
	private boolean storeVarGlobal = false;


	//a = 1;
    public IRAllocate(String name, Variable value)
    {
        this.nodeType = "Allocate";
        this.name = name;
        if(value.getValue() != null)
            rhs = new IRConstant(value.getValue().toString());
        else
            rhs = new IRLoad(value);
    }

    //a[i] = 5;     //a = b[5];
    IRAllocate(Variable name, Variable at, Variable value)
    {
        if(name.getType().equals(Type.VARIABLE)) {
            this.nodeType = "Allocate";
            this.name = name.getVar();
            rhs = new IRLoad(value, at);
        }
        else {
            this.nodeType = "Allocate";
            this.name = name.getVar();
            //TODO
        }
    }

    //a[i] = b[5];
    IRAllocate(String name, Variable varAt, Variable value, Variable valueAt)
    {
        this.nodeType = "Allocate";
        this.name = name;

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
		    this.register = getVarIfExists(this.name);
		    initRegister();
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
    	
    	String storeInst = getStoreInst();
    	inst.add(storeInst);

        return inst;
    }

	private String getStoreInst()
	{
		if (this.storeVarGlobal)
		{
			IRModule module = (IRModule)findParent("Module");
			String varType = type == Type.INTEGER ? "I" : "A";
			return "putstatic " + module.getName() + "/" + name + " " + varType;
		}
		else
		{
            return "istore " + this.register;
		}
	}

	private boolean storeVarIsGlobal()
	{
		IRModule module = (IRModule)findParent("Module");
		return module.getGlobal(name) != null;
	}

	private int getVarIfExists(String varName)
	{
		IRMethod method = (IRMethod)this.parent;
		ArrayList<IRNode> children = method.getChildren();
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).toString().equals("Allocate"))
			{
				IRAllocate alloc = (IRAllocate)children.get(i);
				if (alloc.getName().equals(varName) && alloc.getRegister() != -1)
					return alloc.getRegister();
			}
		}
		return -1;
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
