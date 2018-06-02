package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import yal2jvm.hlir.IRAllocate;
import yal2jvm.hlir.IRArith;
import yal2jvm.hlir.IRCall;
import yal2jvm.hlir.IRComparison;
import yal2jvm.hlir.IRLoad;
import yal2jvm.hlir.IRMethod;
import yal2jvm.hlir.IRModule;
import yal2jvm.hlir.IRNode;
import yal2jvm.hlir.IRReturn;
import yal2jvm.hlir.IRStoreArith;
import yal2jvm.hlir.IRStoreCall;
import yal2jvm.hlir.Variable;
import yal2jvm.utils.Utils;

public class MethodTree extends Method
{
	private IRMethod node;
	private HashMap<String, Integer> varToBit;
	private ArrayList<Line> lines;
	private ArrayList<String> locals;
	private int lineCount = 0;
	private Line currBranching = null;

	public MethodTree(IRMethod method)
	{
		this.node = method;
		this.lines = new ArrayList<>();
		this.varToBit = new HashMap<>();
	}
	
	@Override
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

	@Override
	public String getName()
	{
		return this.node.getName();
	}

	@Override
	public void buildAllLines()
	{
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
			}
			this.lines.add(line);
		}
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
		// TODO Auto-generated method stub
		
	}
}
