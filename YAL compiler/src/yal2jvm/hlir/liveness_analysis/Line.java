package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Line
{
	private int id;
	private HashMap<String, Integer> varToBit;
	private BitSet use;
	private BitSet def;
	private BitSet in;
	private BitSet out;
	private ArrayList<Line> successors;

	public Line(int id, HashMap<String, Integer> varToBit)
	{
		this.id = id;
		this.varToBit = varToBit;
		this.use = new BitSet();
		this.def = new BitSet();
		this.in = new BitSet();
		this.out = new BitSet();
		this.successors = new ArrayList<>();
	}
	
	@Override
	public String toString()
	{
		String s = "Line " + this.id + " ";
		s += "USE: [" + stringifySet(this.use) + "] ";
		s += "DEF: [" + stringifySet(this.def) + "] ";
		s += "IN:  [" + stringifySet(this.in) + "] ";
		s += "OUT: [" + stringifySet(this.out) + "] ";
		return s;
	}
	
	private String stringifySet(BitSet set)
	{
		String s = "";
		for (String key : this.varToBit.keySet())
		{
			int i = varToBit.get(key);
			if (set.get(i))
				s += key + ", ";
		}
		if (s.length() > 0)
			s = s.substring(0, s.length() - 2);
		return s;
	}
	
	public void addUse(String var)
	{
		int index = this.varToBit.get(var);
		this.use.set(index);
	}
	
	public void addDef(String var)
	{
		int index = this.varToBit.get(var);
		this.def.set(index);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public void addSuccessor(Line line)
	{
		this.successors.add(line);
	}
}
