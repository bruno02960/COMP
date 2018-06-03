package yal2jvm.hlir.liveness_analysis;

import java.io.Serializable;
import java.util.ArrayList;

public class IntNode implements Serializable
{
	private String name;
	private ArrayList<IntNode> interferences;

	public IntNode(String name)
	{
		this.name = name;
		this.interferences = new ArrayList<>();
	}
	
	public void addInterference(IntNode node)
	{
		if (node.getName().equals(this.name))
			return;
		if (this.interferences.indexOf(node) == -1)
			this.interferences.add(node);
	}

	public void removeInterference(IntNode node)
	{
		this.interferences.remove(node);
	}

	@Override
	public boolean equals(Object o)
	{
		return this.name.equals(((IntNode)o).getName());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ArrayList<IntNode> getInterferences()
	{
		return interferences;
	}

	public int indegree()
	{
		return this.interferences.size();
	}
	
	@Override
	public String toString()
	{
		String s = this.name + " --> [";
		for (int i = 0; i < this.interferences.size(); i++)
			s += this.interferences.get(i).getName() + ", ";
		if (this.interferences.size() > 0)
			s = s.substring(0, s.length() - 2);
		s += "]";
		return s;
	}
}
