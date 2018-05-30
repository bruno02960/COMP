package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;

public class IntNode
{
	private String name;
	ArrayList<IntNode> interferences;
	
	public IntNode(String name)
	{
		this.name = name;
		this.interferences = new ArrayList<>();
	}
	
	public void addInterference(IntNode node)
	{
		this.interferences.add(node);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this.name == ((IntNode)o).getName();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public int indegree()
	{
		return this.interferences.size();
	}
}
