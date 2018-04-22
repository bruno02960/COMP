package yal2jvm.HHIR;

import java.util.ArrayList;

public abstract class IRNode
{
	protected IRNode parent;
	protected ArrayList<IRNode> children;
	protected String nodeType;
	
	public void addChild(IRNode child)
	{
		getChildren().add(child);
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
	
	@Override
	public String toString()
	{
		return this.nodeType;
	}
}
