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
			
		//newVar1;
		//newVar2 = newVar1 * var3;
			IRMethod m1 = new IRMethod("method1", Type.VOID, null, new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}, new String[]{"var1", "var2", "var3"});
				m1.addChild(new IRAllocate("newVar1", Type.INTEGER, null));
					IRStoreArith arith1 = new IRStoreArith("newVar2", Operation.MULT);
					arith1.setRhs(new IRLoad("newVar1"));
					arith1.setLhs(new IRLoad("var3"));
				m1.addChild(arith1);
		module.addChild(m1);
		
		
		//var1 = var2 * var3;
			IRMethod m2 = new IRMethod("method2", Type.VOID, null, new Type[]{Type.INTEGER, Type.INTEGER, Type.INTEGER}, new String[]{"var1", "var2", "var3"});
					IRStoreArith arith2 = new IRStoreArith("var1", Operation.MULT);
					arith2.setRhs(new IRLoad("var2"));
					arith2.setLhs(new IRLoad("var3"));
				m2.addChild(arith2);
		module.addChild(m2);
		
		
		
			IRMethod main = new IRMethod("main", Type.VOID, "ret", null, null);
		module.addChild(main);
		
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
