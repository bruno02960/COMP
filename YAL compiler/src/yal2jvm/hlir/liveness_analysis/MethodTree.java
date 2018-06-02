package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.TreeSet;

import yal2jvm.hlir.IRAllocate;
import yal2jvm.hlir.IRLoad;
import yal2jvm.hlir.IRMethod;
import yal2jvm.hlir.IRModule;
import yal2jvm.hlir.IRNode;
import yal2jvm.hlir.IRStoreArith;
import yal2jvm.hlir.IRStoreCall;
import yal2jvm.hlir.Variable;
import yal2jvm.utils.Utils;

public class MethodTree extends Method
{
	private IRMethod node;

	public MethodTree(IRMethod method)
	{
		this.node = method;
	}
	
	@Override
	public ArrayList<String> getAllVars()
	{
		IRModule module = (IRModule)this.node.findParent("Module");
		var globals = module.getAllGlobals();
		var locals = findLocals();
		locals.removeAll(globals);
		return Utils.setToList(locals);
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

	@Override
	public Line getNextLine()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return this.node.getName();
	}

}
