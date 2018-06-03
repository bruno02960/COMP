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

public class MethodSetBuilder
{
	private IRMethod node;
	private HashMap<String, Integer> varToBit;
	private ArrayList<Line> lines;
	private ArrayList<String> locals;
	private int lineCount = 0;
	
	public MethodSetBuilder(IRMethod method)
	{
		this.node = method;
		this.lines = new ArrayList<>();
		this.varToBit = new HashMap<>();
	}

	public ArrayList<String> getAllVars()
	{
		IRModule module = (IRModule)this.node.findParent("Module");
		var globals = module.getAllGlobals();
		var locals = findLocals();
		locals.removeAll(globals);
		var list = Utils.setToList(locals);
		
		for (int i = 0; i < list.size(); i++)
			varToBit.put(list.get(i), i);
		
		this.locals = list;
		return list;
	}

	private TreeSet<String> findLocals()
	{
		var locals = new TreeSet<String>();
		
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

	public Line getLine(int id)
	{
		for (Line line : this.lines)
		{
			if (line.getId() == id)
				return line;
		}
		return null;
	}

	public String getName()
	{
		return this.node.getName();
	}

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

	private Line createMethodArgumentsLine()
	{
		Line line = new Line(this.lineCount, this.varToBit);
		this.lineCount++;
		
		Variable[] args = this.node.getArgs();
		for (Variable arg : args)
			line.addDef(arg.getVar());
		
		return line;
	}

	private void setSuccessors()
	{
		for (int i = 0; i < this.lines.size() - 1; i++)
		{
			Line currLine = this.lines.get(i);
			
			if (!currLine.getJumpLabel().equals(""))
			{
				currLine.addSuccessor(this.lines.get(i + 1));
				Line dest = findLabelLine(currLine.getJumpLabel());
				currLine.addSuccessor(dest);
			}
			else if (currLine.isJump())
			{
				Line dest = findLabelLine(currLine.getJumpLabel());
				currLine.addSuccessor(dest);
			}
			else
			{
				currLine.addSuccessor(this.lines.get(i + 1));
			}
		}
	}

	private Line findLabelLine(String label)
	{
		for (Line line : this.lines)
		{
			if (line.getLabel().equals(label))
				return line;
		}
		return null;
	}

	private void buildLineJump(IRJump node, Line line)
	{
		line.setJump(true);
	}

	private void buildLineLabel(IRLabel node, Line line)
	{
		line.addLabel(node.getLabel());
	}

	private void buildLineCall(IRCall node, Line line)
	{
		ArrayList<Variable> args = node.getArguments();
		for (Variable arg : args)
		{
			if (isNotGlobal(arg.getVar()))
				line.addUse(arg.getVar());
		}
	}

	private void buildLineComparison(IRComparison node, Line line)
	{
		line.setJumpLabel(node.getLabel());
		
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

	private void buildLineReturn(IRReturn node, Line line)
	{
		if (isNotGlobal(node.getName()))
		{
			line.addUse(node.getName());
		}
	}

	private void buildLineStoreCall(IRStoreCall node, Line line)
	{
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

	private void buildLineStoreArith(IRStoreArith node, Line line)
	{
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

	private void buildLineAllocate(IRAllocate node, Line line)
	{
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
	
	private boolean isNotGlobal(String var)
	{
		return this.locals.indexOf(var) != -1;
	}

	public ArrayList<Line> getLines()
	{
		return lines;
	}

	public void setLines(ArrayList<Line> lines)
	{
		this.lines = lines;
	}

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

	private boolean compareSetLists(ArrayList<BitSet> oldSet, ArrayList<BitSet> newSet)
	{
		for (int i = 0; i < oldSet.size(); i++)
		{
			if (!oldSet.get(i).equals(newSet.get(i)))
				return false;
		}
		return true;
	}

	private ArrayList<BitSet> getAllOutSets()
	{
		ArrayList<BitSet> sets = new ArrayList<>();
		
		for (int i = 0; i < this.lines.size(); i++)
			sets.add((BitSet)this.lines.get(i).getOut().clone());
		return sets;
	}

	private ArrayList<BitSet> getAllInSets()
	{
		ArrayList<BitSet> sets = new ArrayList<>();
		
		for (int i = 0; i < this.lines.size(); i++)
			sets.add((BitSet)this.lines.get(i).getIn().clone());
		return sets;
	}

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
	
	private BitSet calculateOut(Line line)
	{
		BitSet out = new BitSet(varToBit.size());
		
		for (int i = 0; i < line.getSuccessors().size(); i++)
			out.or(line.getSuccessors().get(i).getIn());
		
		return out;
	}
	
	private BitSet calculateIn(Line line)
	{
		BitSet out = line.getOut();
		BitSet def = line.getDef();
		BitSet use = line.getUse();
		BitSet in = new BitSet(varToBit.size());
		
		in.or(out);
		difference(in, def);
		in.or(use);
		
		return in;
	}

	private void difference(BitSet in, BitSet def)
	{
		for (int i = 0; i < in.size(); i++)
		{
			if (def.get(i))
				in.clear(i);
		}
	}

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
}
