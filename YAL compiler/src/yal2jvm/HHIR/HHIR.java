package yal2jvm.HHIR;

import java.util.ArrayList;

import yal2jvm.ast.SimpleNode;

public class HHIR
{
	private IRModule root;
	private SimpleNode ast;
	
	public HHIR(SimpleNode ast)
	{
		this.ast = ast;
		this.root = createHHIR();
	}

	private IRModule createHHIR()
	{
		//create HHIR from AST
		//hardcoded example for now
		IRModule module = new IRModule("Module1");
		module.addChild(new IRGlobal("a", Type.INTEGER, null));
		module.addChild(new IRGlobal("b", Type.INTEGER, null));
		module.addChild(new IRGlobal("c", Type.INTEGER, 12));
		module.addChild(new IRGlobal("d", Type.INTEGER, 12345));
		module.addChild(new IRMethod("method1", Type.INTEGER, null));
		module.addChild(new IRMethod("method2", Type.VOID, new Type[]{Type.INTEGER}));
		module.addChild(new IRMethod("method3", Type.VOID, new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}));
		
			IRMethod method = new IRMethod("main", Type.VOID, null);
			method.addChild(new IRAllocate("var1", Type.INTEGER, null));
			method.addChild(new IRAllocate("var2", Type.INTEGER, 10));
			method.addChild(new IRAllocate("var3", Type.INTEGER, 20000));
		
				IRStoreArith arith = new IRStoreArith("var1", Operation.ADD);
				arith.setRhs(new IRConstant(100));
				arith.setLhs(new IRConstant(200));
		
			method.addChild(arith);
			method.addChild(new IRReturn(null, null));
		
		module.addChild(method);
		return module;
	}

	public void optimize()
	{
		//use this only as a wrapper for (possibly static) methods of a separate class
		//in order to avoid putting too much logic in this single class
		
	}
	
	public void dataflowAnalysis()
	{
		//use this only as a wrapper for (possibly static) methods of a separate class
		//in order to avoid putting too much logic in this single class
	}
	
	public void allocateRegisters(int maxLocals)
	{
		//use this only as a wrapper for (possibly static) methods of a separate class
		//in order to avoid putting too much logic in this single class
	}
	
	public ArrayList<String> selectInstructions()
	{
		return root.getInstructions();
	}

	public String getModuleName()
	{
		return this.root.getName();
	}
}
