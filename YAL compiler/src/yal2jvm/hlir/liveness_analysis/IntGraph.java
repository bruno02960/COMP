package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;

public class IntGraph
{
	private ArrayList<IntNode> nodes;
	
	public IntGraph()
	{
		this.nodes = new ArrayList<>();
	}
	
	public void addInterference(String var1, String var2)
	{
		IntNode n1 = findNode(var1);
		IntNode n2 = findNode(var2);
		
		n1.addInterference(n2);
		n2.addInterference(n1);
	}
	
	public void addVariable(String var)
	{
		for (IntNode n : this.nodes)
		{
			if (n.getName() == var)
				return;
		}
		IntNode node = new IntNode(var);
		this.nodes.add(node);
	}

	private IntNode findNode(String var)
	{
		for (IntNode n : this.nodes)
		{
			if (n.getName() == var)
				return n;
		}
		IntNode node = new IntNode(var);
		this.nodes.add(node);
		return node;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for (int i = 0; i < this.nodes.size(); i++)
			s += this.nodes.get(i).toString() + "\n";
		return s;
	}
}
