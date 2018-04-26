package yal2jvm.HHIR;

import java.util.ArrayList;

import yal2jvm.SymbolTables.SymbolType;
import yal2jvm.SymbolTables.VarSymbol;

import yal2jvm.ast.*;

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

		return root;
	}
	
	public IRModule createHardcoded()
	{
		IRModule module = new IRModule("Module1");
		module.addChild(new IRGlobal("a", Type.ARRAY, null));
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
			if(child instanceof ASTDECLARATION)
				createDeclarationHHIR((ASTDECLARATION) child);
			else
				createFunctionHHIR((ASTFUNCTION) child);
		}
		
		return module;
	}

	private void createFunctionHHIR(ASTFUNCTION astFunction)
	{
		String functionId = astFunction.id;
		Type returnType = null;
		String returnName = null;
		Type[] argumentsTypes = null;
		String[] argumentsNames = null;


		//indicates the index(child num) of the arguments. 0 if no return value, or 1 if has return value
		int argumentsIndex = 0;

		//get return value if existent
		SimpleNode returnValueNode = (SimpleNode) astFunction.jjtGetChild(0);
		if(!(returnValueNode instanceof ASTVARS)) //indicated that is the return variable
		{
			argumentsIndex++;
			if(returnValueNode instanceof ASTSCALARELEMENT)
			{
				ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)returnValueNode;
				returnType = Type.INTEGER;
				returnName = astscalarelement.id;
			}
			else
			{
				ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
				String returnValueId = astarrayelement.id;
			}
		}

		//get arguments if existent
		SimpleNode argumentsNode = (SimpleNode) astFunction.jjtGetChild(argumentsIndex);
		if(argumentsNode == null || !(argumentsNode instanceof ASTVARS))
			return;

		for(int i = 0; i < argumentsNode.jjtGetNumChildren(); i++)
		{
			SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);
			if( child != null)
			{
				VarSymbol varSymbol;
				if(child instanceof ASTSCALARELEMENT)
				{
					ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
					varSymbol = new VarSymbol(astscalarelement.id, SymbolType.INTEGER.toString(), true);
				}
				else
				{
					ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
					varSymbol = new VarSymbol(astarrayelement.id, SymbolType.ARRAY.toString(), true);
				}
				arguments.add(varSymbol);
			}
		}


		IRMethod function = new IRMethod(functionId, returnType, returnName, argumentsTypes, argumentsNames);
		root.addChild(function);

		createStatementsHHIR((ASTSTATEMENTS) returnValueNode, function);
	}

	private void createStatementsHHIR(ASTSTATEMENTS returnValueNode, IRMethod functionHHIR)
	{

	}

    /**
     * Retrieves the name, type and value of the variable
     * @param astdeclaration declaration to analyse
     */
	private void createDeclarationHHIR(ASTDECLARATION astdeclaration) {

	    SimpleNode id = (SimpleNode) astdeclaration.jjtGetChild(0);
	    String name = null;
	    Type type = null;
	    int value = -1;
	    int size = -1;

	    switch (id.toString()) {
			case "SCALARELEMENT":
				ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) id;
				name = astscalarelement.id;

				if(astdeclaration.jjtGetNumChildren() == 2) {
					ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);
					size = astarraysize.integer;
					type = Type.ARRAY;
				}
				else {
					type = Type.INTEGER;
					if(astdeclaration.operator == null && astdeclaration.integer!= null) {
						value = astdeclaration.integer;
					}
					else if(astdeclaration.operator != null && astdeclaration.integer!= null) {
						String str_value = astdeclaration.operator + astdeclaration.integer;
						value = Integer.parseInt(str_value);
					}
				}
				break;
			case "ARRAYELEMENT":
				ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) id;
				name = astarrayelement.id;
				type = Type.ARRAY;

				if(astdeclaration.jjtGetNumChildren() == 2) {
					ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);
					size = astarraysize.integer;
				}
				else {
					String str_value = astdeclaration.operator + astdeclaration.integer;
					value = Integer.parseInt(str_value);
				}
				break;
		}


		//TODO: Debug
		System.out.println("name= " + name);
		System.out.println("type= " + type.toString());
		System.out.println("value= " + value);
		System.out.println("size= " + size + "\n");

		switch (type) {
			case INTEGER:
				//root.addChild(new IRGlobal(name, type, value));
				break;
			case ARRAY:
				//root.addChild(new IRGlobal(name, type, value, size));
				break;
			default:
				System.err.println("Error on adding declaration to HHIR");
				System.exit(-1);
		}
	}
}
