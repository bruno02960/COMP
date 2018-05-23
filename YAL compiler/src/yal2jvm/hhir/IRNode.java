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

    protected String getInstructionToLoadRegisterToStack(int registerNumber)
    {
        if(registerNumber < 4)
            return "iload_" + registerNumber;
        else
            return "iload " + registerNumber;
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
}