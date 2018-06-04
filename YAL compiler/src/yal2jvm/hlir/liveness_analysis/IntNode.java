package yal2jvm.hlir.liveness_analysis;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class IntNode implements Serializable
{
	private String name;
	private ArrayList<IntNode> interferences;
	private int requiredRegister = -1;

	/**
	 *
	 * @param name
	 */
	public IntNode(String name)
	{
		this.name = name;
		this.interferences = new ArrayList<>();
	}

	/**
	 *
	 * @param node
	 */
	public void addInterference(IntNode node)
	{
		if (node.getName().equals(this.name))
			return;
		if (this.interferences.indexOf(node) == -1)
			this.interferences.add(node);
	}

	/**
	 *
	 * @param node
	 */
	public void removeInterference(IntNode node)
	{
		this.interferences.remove(node);
	}

	/**
	 *
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o)
	{
		return this.name.equals(((IntNode)o).getName());
	}

	/**
	 *
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<IntNode> getInterferences()
	{
		return interferences;
	}

	/**
	 *
	 * @return
	 */
	public int indegree()
	{
		return this.interferences.size();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		String s = this.name + " --> [";
		for (int i = 0; i < this.interferences.size(); i++)
			s += this.interferences.get(i).getName() + ", ";
		if (this.interferences.size() > 0)
			s = s.substring(0, s.length() - 2);
		s += "]";
		if (this.requiredRegister != -1)
			s += " Required reg: " + this.requiredRegister;
		return s;
	}

	/**
	 * 
	 * @return
	 */
	public int getRequiredRegister()
	{
		return requiredRegister;
	}

	/**
	 * 
	 * @param requiredRegister
	 */
	public void setRequiredRegister(int requiredRegister)
	{
		this.requiredRegister = requiredRegister;
	}
}
