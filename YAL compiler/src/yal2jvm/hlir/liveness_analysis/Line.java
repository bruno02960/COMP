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
	private String label = "";
	private String jumpLabel = "";
	private boolean isJump = false;

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
		s += "SUCC: [" + getSuccString() + "]";
		return s;
	}
	
	private String getSuccString()
	{
		String s = "";
		for (Line line : this.successors)
		{
			s += line.getId() + ", ";
		}
		if (s.length() > 0)
			s = s.substring(0, s.length() - 2);
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
	
	public void addLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public void addSuccessor(Line line)
	{
		this.successors.add(line);
	}

	public void setJump(boolean b)
	{
		this.isJump  = b;
	}

	public String getJumpLabel()
	{
		return jumpLabel;
	}

	public void setJumpLabel(String jumpLabel)
	{
		this.jumpLabel = jumpLabel;
	}

	public boolean isJump()
	{
		return this.isJump;
	}

	public ArrayList<Line> getSuccessors()
	{
		return successors;
	}

	public void setSuccessors(ArrayList<Line> successors)
	{
		this.successors = successors;
	}

	public BitSet getUse()
	{
		return use;
	}

	public void setUse(BitSet use)
	{
		this.use = use;
	}

	public BitSet getDef()
	{
		return def;
	}

	public void setDef(BitSet def)
	{
		this.def = def;
	}

	public BitSet getIn()
	{
		return in;
	}

	public void setIn(BitSet in)
	{
		this.in = in;
	}

	public BitSet getOut()
	{
		return out;
	}

	public void setOut(BitSet out)
	{
		this.out = out;
	}
}
