package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 *
 */
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
	private boolean hasSuccessor = true;
	private String type;

	/**
	 *
	 * @param id
	 * @param varToBit
	 */
	public Line(int id, HashMap<String, Integer> varToBit)
	{
		this.id = id;
		this.varToBit = varToBit;
		this.use = new BitSet(this.varToBit.size());
		this.def = new BitSet(this.varToBit.size());
		this.in = new BitSet(this.varToBit.size());
		this.out = new BitSet(this.varToBit.size());
		this.successors = new ArrayList<>();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		String s = "Line " + this.id + " (" + this.type + ") -> "; 
		s += "USE: [" + stringifySet(this.use) + "] ";
		s += "DEF: [" + stringifySet(this.def) + "] ";
		s += "IN:  [" + stringifySet(this.in) + "] ";
		s += "OUT: [" + stringifySet(this.out) + "] ";
		s += "SUCC: [" + getSuccString() + "]";
		return s;
	}

	/**
	 *
	 * @return
	 */
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

	/**
	 *
	 * @param set
	 * @return
	 */
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

	/**
	 *
	 * @param var
	 */
	public void addUse(String var)
	{
		int index = this.varToBit.get(var);
		this.use.set(index);
	}

	/**
	 *
	 * @param var
	 */
	public void addDef(String var)
	{
		int index = this.varToBit.get(var);
		this.def.set(index);
	}

	/**
	 *
	 * @return
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *
	 * @param id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 *
	 * @param label
	 */
	public void addLabel(String label)
	{
		this.label = label;
	}

	/**
	 *
	 * @return
	 */
	public String getLabel()
	{
		return this.label;
	}

	/**
	 *
	 * @param line
	 */
	public void addSuccessor(Line line)
	{
		this.successors.add(line);
	}

	/**
	 *
	 * @param b
	 */
	public void setJump(boolean b)
	{
		this.isJump  = b;
	}

	/**
	 *
	 * @return
	 */
	public String getJumpLabel()
	{
		return jumpLabel;
	}

	/**
	 *
	 * @param jumpLabel
	 */
	public void setJumpLabel(String jumpLabel)
	{
		this.jumpLabel = jumpLabel;
	}

	/**
	 *
	 * @return
	 */
	public boolean isJump()
	{
		return this.isJump;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Line> getSuccessors()
	{
		return successors;
	}

	/**
	 *
	 * @param successors
	 */
	public void setSuccessors(ArrayList<Line> successors)
	{
		this.successors = successors;
	}

	/**
	 *
	 * @return
	 */
	public BitSet getUse()
	{
		return use;
	}

	/**
	 *
	 * @param use
	 */
	public void setUse(BitSet use)
	{
		this.use = use;
	}

	/**
	 *
	 * @return
	 */
	public BitSet getDef()
	{
		return def;
	}

	/**
	 *
	 * @param def
	 */
	public void setDef(BitSet def)
	{
		this.def = def;
	}

	/**
	 *
	 * @return
	 */
	public BitSet getIn()
	{
		return in;
	}

	/**
	 *
	 * @param in
	 */
	public void setIn(BitSet in)
	{
		this.in = in;
	}

	/**
	 *
	 * @return
	 */
	public BitSet getOut()
	{
		return out;
	}

	/**
	 *
	 * @param out
	 */
	public void setOut(BitSet out)
	{
		this.out = out;
	}

	public boolean hasSuccessor()
	{
		return this.hasSuccessor;
	}

	public void setHasSuccessor(boolean hasSuccessor)
	{
		this.hasSuccessor = hasSuccessor;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
