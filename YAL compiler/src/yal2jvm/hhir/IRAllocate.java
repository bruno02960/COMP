package yal2jvm.hhir;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{

    private String name;
    private Type type;
    private Integer value;
    private int register = -1;
    int size = -1;
    private String variable;

    public IRAllocate(String name, Type type, Integer value)
    {
        this.nodeType = "Allocate";
        this.name = name;
        assert type == Type.INTEGER;
        this.type = type;
        this.value = value == null ? 0 : value;
    }

    /*
    TODO: Generate code for this
     */
    public IRAllocate(String name, Type type, String variable)
    {
        this.nodeType = "Allocate";
        this.name = name;
        assert type == Type.INTEGER;
        this.type = type;
        this.variable = variable;
    }

    public IRAllocate(String name, Type type, Integer value, Integer size)
    {
        this.nodeType = "Allocate";
        this.name = name;
        assert type == Type.ARRAY;
        this.type = type;
        this.value = value == null ? 0 : value;
        this.size = size;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        
        this.register = getVarIfExists(this.name);
        initRegister();
 
        switch (type)
        {
            case INTEGER:
            {
            	if (this.variable != null)
            	{
            		int otherReg = getVarIfExists(this.variable);
            		if (otherReg != -1)
            			inst.add("iload " + otherReg);
            		else
            		{
            			IRLoad global = new IRLoad(this.variable);
            			this.addChild(global);
            			inst.addAll(global.getInstructions());
            		}
            	}
            	else
            		inst.add(IRConstant.getLoadConstantInstruction(this.value));
            	
                inst.add("istore " + this.register);
                break;
            }
            case ARRAY:
                break;

            default:
                break;
        }

        return inst;
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
        if (this.register == -1)
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
