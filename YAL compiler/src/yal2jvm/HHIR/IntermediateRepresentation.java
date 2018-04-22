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
		nodes.add(this.getRoot());
	}

	public String getModuleName()
	{
		return getRoot().getName();
	}

	public IRModule getRoot()
	{
		return root;
	}

	public void setRoot(IRModule root)
	{
		this.root = root;
	}
	
	public ArrayList<String> selectInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		inst.addAll(root.getInstructions());
		for (int i = 0; i < root.getChildren().size(); i++)
			inst.addAll(root.getChildren().get(i).getInstructions());
		return inst;
	}

}
