package yal2jvm.HHIR;

import java.util.ArrayList;

import yal2jvm.ast.ASTDECLARATION;
import yal2jvm.ast.ASTMODULE;
import yal2jvm.ast.Node;
import yal2jvm.ast.SimpleNode;

public class HHIR
{
	private IRModule root;
	private SimpleNode ast;
	
	public HHIR(SimpleNode ast)
	{
		this.ast = ast;
		this.root = createHHIR();
		
		//hardcoded
		this.root = createHardcoded();
	}

	private IRModule createHHIR()
	{
		//create HHIR from AST
		//hardcoded example for now
		ASTMODULE astModule = (ASTMODULE) ast;
		root = createModuleHHIR(astModule);
		IRModule module = new IRModule("Module1");
		return module;
	}
	
	public IRModule createHardcoded()
	{
		IRModule module = new IRModule("Module1");
		module.addChild(new IRGlobal("a", Type.INTEGER, null));
		module.addChild(new IRGlobal("b", Type.INTEGER, null));
		module.addChild(new IRGlobal("c", Type.INTEGER, 12));
		module.addChild(new IRGlobal("d", Type.INTEGER, 12345));
			
			IRMethod m1 = new IRMethod("method1", Type.VOID, "ret", new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}, new String[]{"var1", "var2", "var3"});
			m1.addChild(new IRReturn(null, null));
		module.addChild(m1);
		
			IRMethod m2 = new IRMethod("method2", Type.VOID, "ret", new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}, new String[]{"var1", "var2", "var3"});
				m2.addChild(new IRAllocate("aNewVar", Type.INTEGER, 50));
				IRStoreArith arith = new IRStoreArith("var2", Operation.MULT);
				//arith.setLhs(lhs);
			
			m2.addChild(new IRReturn(null, null));
		module.addChild(m2);
		
		
		
			IRMethod method = new IRMethod("main", Type.VOID, "ret", null, null);
			method.addChild(new IRAllocate("var1", Type.INTEGER, null));
			method.addChild(new IRAllocate("var2", Type.INTEGER, 10));
			method.addChild(new IRAllocate("var3", Type.INTEGER, 20000));
		
				IRStoreArith arith1 = new IRStoreArith("var1", Operation.ADD);
				arith1.setRhs(new IRConstant(100));
				arith1.setLhs(new IRConstant(200));
		
			method.addChild(arith1);
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

	private IRModule createModuleHHIR(ASTMODULE astModule)
	{
		String moduleName = astModule.name;
		IRModule module = new IRModule(moduleName);

		int moduleNumberChilds = astModule.jjtGetNumChildren();
		for(int i = 0; i < moduleNumberChilds; i++)
		{
			Node child = astModule.jjtGetChild(i);
		//	if(child instanceof ASTDECLARATION)
		//		createDeclarationHHIR((ASTDECLARATION) child));

		}
		module.addChild(new IRGlobal("a", Type.INTEGER, null));
		module.addChild(new IRGlobal("b", Type.INTEGER, null));
		module.addChild(new IRGlobal("c", Type.INTEGER, 12));
		module.addChild(new IRGlobal("d", Type.INTEGER, 12345));
		
		return null;
	}

	private void createDeclarationHHIR(ASTDECLARATION child)
	{

	}
}
