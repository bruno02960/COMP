package yal2jvm.HHIR;

import java.util.ArrayList;

public class IntermediateRepresentation 
{
	private ArrayList<IRNode> nodes;
	private IRModule root;

	public IntermediateRepresentation(String moduleName)
	{
		this.nodes = new ArrayList<IRNode>();
		this.root = new IRModule(moduleName);
		nodes.add(this.root);
	}
	
	public ArrayList<String> selectInstructions()
	{
		return root.getInstructions();
	}

	public String getModuleName()
	{
		return root.getName();
	}
}
