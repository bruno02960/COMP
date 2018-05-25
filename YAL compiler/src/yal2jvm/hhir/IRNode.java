package yal2jvm.hhir;

import java.util.ArrayList;

public abstract class IRNode
{
    protected IRNode parent;
    protected ArrayList<IRNode> children;
    protected String nodeType;

    public IRNode()
    {
        children = new ArrayList<IRNode>();
    }

    public void addChild(IRNode child)
    {
        children.add(child);
        child.setParent(this);
    }

    public IRNode getParent()
    {
        return parent;
    }

    public void setParent(IRNode parent)
    {
        this.parent = parent;
    }

    public ArrayList<IRNode> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<IRNode> children)
    {
        this.children = children;
    }

    public abstract ArrayList<String> getInstructions();

    private String getInstructionLoadOrStoreInstructionMoreEfficient(String instruction, int registerNumber)
    {
        if(registerNumber < 4)
            return instruction + "_" + registerNumber;
        else
            return instruction + " " + registerNumber;
    }

    protected String getInstructionToLoadIntFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("iload", registerNumber);
    }

    protected String getInstructionToStoreIntInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("istore", registerNumber);
    }

    protected String getInstructionToLoadArrayFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("aload", registerNumber);
    }

    protected String getInstructionToStoreArrayInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("astore", registerNumber);
    }

    @Override
    public String toString()
    {
        return this.nodeType;
    }
    
    public IRNode findParent(String nodeType)
    {
        IRNode res = null;
        IRNode par = this.parent;
        while (true)
        {
            if (par.toString().equals(nodeType))
            {
                res = par;
                break;
            } 
            else
            {
                par = par.getParent();
                if (par == null)
                {
                	return null;
                }
            }
        }
        return res;
    }

    protected IRAllocate getVarIfExists(String varName)
    {
        IRNode parent = this.parent;

        while(parent.nodeType != "Method") {
            parent = parent.parent;
        }

        IRMethod method = (IRMethod)parent;
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

    protected ArrayList<String> setArrayElement(IRNode index, int register)
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add(getInstructionToLoadArrayFromRegisterToStack(register));
        inst.addAll(index.getInstructions());
        inst.add("iastore");

        return inst;
    }
}
