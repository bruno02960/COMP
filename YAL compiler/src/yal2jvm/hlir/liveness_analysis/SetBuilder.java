package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.TreeSet;

import yal2jvm.hlir.IRAllocate;
import yal2jvm.hlir.IRArith;
import yal2jvm.hlir.IRCall;
import yal2jvm.hlir.IRComparison;
import yal2jvm.hlir.IRJump;
import yal2jvm.hlir.IRLabel;
import yal2jvm.hlir.IRLoad;
import yal2jvm.hlir.IRMethod;
import yal2jvm.hlir.IRModule;
import yal2jvm.hlir.IRNode;
import yal2jvm.hlir.IRReturn;
import yal2jvm.hlir.IRStoreArith;
import yal2jvm.hlir.IRStoreCall;
import yal2jvm.hlir.Variable;
import yal2jvm.utils.Utils;

/**
 *
 */
public class SetBuilder
{
	private IRMethod node;
	private HashMap<String, Integer> varToBit;
	private ArrayList<Line> lines;
	private ArrayList<String> locals;
	private int lineCount = 0;

	/**
	 *
	 * @param method
	 */
	public SetBuilder(IRMethod method)
	{
		this.node = method;
		this.lines = new ArrayList<>();
		this.varToBit = new HashMap<>();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<String> getAllVars()
	{
		IRModule module = (IRModule)this.node.findParent("Module");
		TreeSet<String> globals = module.getAllGlobals();
		TreeSet<String> locals = findLocals();
		locals.removeAll(globals);
		ArrayList<String> list = Utils.setToList(locals);

		for (int i = 0; i < list.size(); i++)
			varToBit.put(list.get(i), i);
		
		this.locals = list;
		return list;
	}

	/**
	 *
	 * @return
	 */
	private TreeSet<String> findLocals()
	{
		TreeSet<String> locals = new TreeSet<String>();
		
		for (IRNode n : this.node.getChildren())
		{
			switch (n.getNodeType())
			{
				case "Allocate":
				{
					IRAllocate alloc = (IRAllocate)n;
					locals.add(alloc.getName());
					if (alloc.getRhs().getNodeType().equals("Load"))
					{
						IRLoad load = (IRLoad)alloc.getRhs();
						locals.add(load.getName());
					}
					break;
				}
				case "StoreArith":
				{
					IRStoreArith arith = (IRStoreArith)n;
					locals.add(arith.getName());
					break;
				}
				case "StoreCall":
				{
					IRStoreCall call = (IRStoreCall)n;
					locals.add(call.getName());
					break;
				}
			}
		}
		Variable[] args = this.node.getArgs();
		for (Variable arg : args)
			locals.add(arg.getVar());
		
		return locals;
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public Line getLine(int id)
	{
		for (Line line : this.lines)
		{
			if (line.getId() == id)
				return line;
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public String getName()
	{
		return this.node.getName();
	}

	/**
	 *
	 */
	public void buildAllLines()
	{
		this.lines.add(createMethodArgumentsLine());
		
		for (IRNode n : this.node.getChildren())
		{
			Line line = new Line(this.lineCount, this.varToBit);
			this.lineCount++;
			
			switch (n.getNodeType())
			{
				case "Allocate":
					buildLineAllocate((IRAllocate)n, line);
					break;
				case "StoreArith":
					buildLineStoreArith((IRStoreArith)n, line);
					break;
				case "StoreCall":
					buildLineStoreCall((IRStoreCall)n, line);
					break;
				case "Return":
					buildLineReturn((IRReturn)n, line);
					break; 
				case "Comparison":
					buildLineComparison((IRComparison)n, line);
					break;
				case "Call":
					buildLineCall((IRCall)n, line);
					break;
				case "Label":
					buildLineLabel((IRLabel)n, line);
					break;
				case "Jump":
					buildLineJump((IRJump)n, line);
					break;
			}
			this.lines.add(line);
		}
		setSuccessors();
	}

	/**
	 *
	 * @return
	 */
	private Line createMethodArgumentsLine()
	{
		Line line = new Line(this.lineCount, this.varToBit);
		this.lineCount++;
		
		Variable[] args = this.node.getArgs();
		for (Variable arg : args)
			line.addDef(arg.getVar());
		
		return line;
	}

	/**
	 *
	 */
	private void setSuccessors()
	{
		for (int i = 0; i < this.lines.size() - 1; i++)
		{
			Line currLine = this.lines.get(i);
			
			//line has a jump
			if (currLine.isJump())
			{
				Line dest = findLabelLine(currLine.getJumpLabel());
				currLine.addSuccessor(dest);
			}
			//line has a direct successor
			if (currLine.hasSuccessor())
			{
				currLine.addSuccessor(this.lines.get(i + 1));
			}
			//it can have both a jump and a direct successor (like an if)
		}
	}

	/**
	 *
	 * @param label
	 * @return
	 */
	private Line findLabelLine(String label)
	{
		for (Line line : this.lines)
		{
			if (line.getLabel().equals(label))
				return line;
		}
		return null;
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineJump(IRJump node, Line line)
	{
		line.setJump(true);
		line.setJumpLabel(node.getLabel());
		line.setHasSuccessor(false);
		line.setType("Jump");
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineLabel(IRLabel node, Line line)
	{
		line.addLabel(node.getLabel());
		line.setType("Label");
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineCall(IRCall node, Line line)
	{
		ArrayList<Variable> args = node.getArguments();
		for (Variable arg : args)
		{
			if (isNotGlobal(arg.getVar()))
				line.addUse(arg.getVar());
		}
		line.setType("Call");
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineComparison(IRComparison node, Line line)
	{
		line.setJump(true);
		line.setJumpLabel(node.getLabel());
		line.setType("Comp");
		
		if (node.getRhs().getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)node.getRhs();
			if (isNotGlobal(load.getName()))
			{
				line.addUse(load.getName());
			}
		}
		if (node.getLhs().getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)node.getLhs();
			if (isNotGlobal(load.getName()))
			{
				line.addUse(load.getName());
			}
		}
		if (node.getLhs().getNodeType().equals("Arith"))
		{
			IRArith arith = (IRArith)node.getLhs();
			if (arith.getRhs().getNodeType().equals("Load"))
			{
				IRLoad load = (IRLoad)node.getRhs();
				if (isNotGlobal(load.getName()))
				{
					line.addUse(load.getName());
				}
			}
			if (arith.getLhs().getNodeType().equals("Load"))
			{
				IRLoad load = (IRLoad)node.getLhs();
				if (isNotGlobal(load.getName()))
				{
					line.addUse(load.getName());
				}
			}
		}	
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineReturn(IRReturn node, Line line)
	{
		if (isNotGlobal(node.getName()))
		{
			line.addUse(node.getName());
			line.setType("Return");
		}
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineStoreCall(IRStoreCall node, Line line)
	{
		line.setType("StoreCall");
		if (isNotGlobal(node.getName()))
		{
			line.addDef(node.getName());
		}
		IRCall call = (IRCall)node.getChildren().get(0);
		ArrayList<Variable> args = call.getArguments();
		for (Variable arg : args)
		{
			if (isNotGlobal(arg.getVar()))
				line.addUse(arg.getVar());
		}
		
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineStoreArith(IRStoreArith node, Line line)
	{
		line.setType("StoreArith");
		if (isNotGlobal(node.getName()))
		{
			line.addDef(node.getName());
		}
		if (node.getRhs().getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)node.getRhs();
			if (isNotGlobal(load.getName()))
			{
				line.addUse(load.getName());
			}
		}
		if (node.getLhs().getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)node.getLhs();
			if (isNotGlobal(load.getName()))
			{
				line.addUse(load.getName());
			}
		}
	}

	/**
	 *
	 * @param node
	 * @param line
	 */
	private void buildLineAllocate(IRAllocate node, Line line)
	{
		line.setType("Allocate");
		if (isNotGlobal(node.getName()))
		{
			line.addDef(node.getName());
		}
		if (node.getRhs().getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)node.getRhs();
			if (isNotGlobal(load.getName()))
			{
				line.addUse(load.getName());
			}
		}
	}

	/**
	 *
	 * @param var
	 * @return
	 */
	private boolean isNotGlobal(String var)
	{
		return this.locals.indexOf(var) != -1;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Line> getLines()
	{
		return lines;
	}

	/**
	 *
	 * @param lines
	 */
	public void setLines(ArrayList<Line> lines)
	{
		this.lines = lines;
	}

	/**
	 *
	 */
	public void calculateSets()
	{
		ArrayList<BitSet> insOld;
		ArrayList<BitSet> outsOld;

		doIteration();
		
		insOld = getAllInSets();
		outsOld = getAllOutSets();
		
		boolean isEqual = false;
		
		while (!isEqual)
		{
			doIteration();
			
			ArrayList<BitSet> insNew = getAllInSets();
			ArrayList<BitSet> outsNew = getAllOutSets();
			
			isEqual = compareSetLists(insOld, insNew) && compareSetLists(outsOld, outsNew);
			
			insOld = insNew;
			outsOld = outsNew;
		}
	}

	/**
	 *
	 * @param oldSet
	 * @param newSet
	 * @return
	 */
	private boolean compareSetLists(ArrayList<BitSet> oldSet, ArrayList<BitSet> newSet)
	{
		for (int i = 0; i < oldSet.size(); i++)
		{
			if (!oldSet.get(i).equals(newSet.get(i)))
				return false;
		}
		return true;
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<BitSet> getAllOutSets()
	{
		ArrayList<BitSet> sets = new ArrayList<>();
		
		for (int i = 0; i < this.lines.size(); i++)
			sets.add((BitSet)this.lines.get(i).getOut().clone());
		return sets;
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<BitSet> getAllInSets()
	{
		ArrayList<BitSet> sets = new ArrayList<>();
		
		for (int i = 0; i < this.lines.size(); i++)
			sets.add((BitSet)this.lines.get(i).getIn().clone());
		return sets;
	}

	/**
	 *
	 */
	private void doIteration()
	{
		for (int i = this.lines.size() - 1; i > -1; i--)
		{
			Line line = this.lines.get(i);
			
			BitSet out = calculateOut(line);
			line.setOut(out);
			
			BitSet in = calculateIn(line);
			
			line.setIn(in);
		}
	}

	/**
	 *
	 * @param line
	 * @return
	 */
	private BitSet calculateOut(Line line)
	{
		BitSet out = new BitSet(varToBit.size());
		
		for (int i = 0; i < line.getSuccessors().size(); i++)
		{
			out.or(line.getSuccessors().get(i).getIn());
		}
		
		return out;
	}

	/**
	 *
	 * @param line
	 * @return
	 */
	private BitSet calculateIn(Line line)
	{
		BitSet out = line.getOut();
		BitSet def = line.getDef();
		BitSet use = line.getUse();	
		BitSet diff = difference(out, def);
		
		BitSet in = new BitSet(varToBit.size());
		
		in.or(use);
		in.or(diff);
		
		return in;
	}

	/**
	 *
	 * @param out
	 * @param def
	 */
	private BitSet difference(BitSet out, BitSet def)
	{
		BitSet diff = new BitSet(varToBit.size());
		
		for (int i = 0; i < out.size(); i++)
		{
			if (out.get(i) && !def.get(i))
				diff.set(i);
		}
		return diff;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<IntPair> getAllPairs()
	{
		ArrayList<IntPair> pairs = new ArrayList<>();
		
		ArrayList<BitSet> ins = getAllInSets();
		for (BitSet set : ins)
			pairs.addAll(getInterferences(set));
		
		ArrayList<BitSet> outs = getAllOutSets();
		for (BitSet set : outs)
			pairs.addAll(getInterferences(set));

		return pairs;
	}

	/**
	 *
	 * @param set
	 * @return
	 */
	private ArrayList<IntPair> getInterferences(BitSet set)
	{
		ArrayList<String> varList = new ArrayList<>();
		ArrayList<IntPair> pairs = new ArrayList<>();
		
		for (String var : this.varToBit.keySet())
		{
			int i = this.varToBit.get(var);
			if (set.get(i))
				varList.add(var);
		}
		if (varList.size() < 2)
			return pairs;
		
		for (int i = 0; i < varList.size(); i++)
		{
			for (int j = 0; j < varList.size(); j++)
			{
				if (i != j)
					pairs.add(new IntPair(varList.get(i), varList.get(j)));
			}
		}
		return pairs;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getAllArgs()
	{
		ArrayList<String> args = new ArrayList<>();
		Variable[] varArgs = this.node.getArgs();
		
		for (Variable arg : varArgs)
			args.add(arg.getVar());
		
		return args;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getLocals()
	{
		return locals;
	}
}
