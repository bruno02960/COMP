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
		createModuleHHIR(astModule);

		return root;
	}
	
	public IRModule createHardcoded()
	{
		IRModule module = new IRModule("Module1");
		module.addChild(new IRGlobal("a", Type.ARRAY, null));
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

	private void createModuleHHIR(ASTMODULE astModule)
	{
		String moduleName = astModule.name;
		root = new IRModule(moduleName);

		int moduleNumberChilds = astModule.jjtGetNumChildren();
		for(int i = 0; i < moduleNumberChilds; i++)
		{
			Node child = astModule.jjtGetChild(i);
			if(child instanceof ASTDECLARATION)
				createDeclarationHHIR((ASTDECLARATION) child);
			else
				createFunctionHHIR((ASTFUNCTION) child);
		}
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
		SimpleNode currNode = (SimpleNode) astFunction.jjtGetChild(0);
		if (!(currNode instanceof ASTVARS) && !(currNode instanceof ASTSTATEMENTS)) //indicated that is the return variable
		{
			argumentsIndex++;
			if (currNode instanceof ASTSCALARELEMENT)
			{
				returnType = Type.INTEGER;
				returnName = ((ASTSCALARELEMENT) currNode).id;
			}
			else
			{
				returnType = Type.ARRAY;
				returnName = ((ASTARRAYELEMENT) currNode).id;
			}
		}

		//get arguments if existent
		currNode = (SimpleNode) astFunction.jjtGetChild(argumentsIndex);
		if (currNode instanceof ASTVARS)
		{
			int numArguments = currNode.jjtGetNumChildren();
			argumentsNames = new String[numArguments];
			argumentsTypes = new Type[numArguments];

			for (int i = 0; i < numArguments; i++)
			{
				SimpleNode child = (SimpleNode) currNode.jjtGetChild(i);
				if (child != null)
				{
					if (child instanceof ASTSCALARELEMENT)
					{
						argumentsNames[i] = ((ASTSCALARELEMENT) child).id;
						argumentsTypes[i] = Type.INTEGER;
					}
					else
					{
						argumentsNames[i] = ((ASTARRAYELEMENT) child).id;
						argumentsTypes[i] = Type.ARRAY;
					}
				}
			}
		}
		IRMethod function = new IRMethod(functionId, returnType, returnName, argumentsTypes, argumentsNames);

		//TODO: Debug
		System.out.println("name= " + functionId);
		if(returnType != null)
			System.out.println("return type= " + returnType.toString());
		if(returnName != null)
			System.out.println("return name= " + returnName);
		if(argumentsTypes != null)
		{
			System.out.println("argumentsTypes= " );
			for(int i = 0; i < argumentsTypes.length; i++)
				System.out.println(argumentsTypes[i]);
		}

		if(argumentsNames != null)
		{
			System.out.println("argumentsNames= " );
			for(int i = 0; i < argumentsNames.length; i++)
				System.out.println(argumentsNames[i]);
		}

		root.addChild(function);

		if(!(currNode instanceof ASTSTATEMENTS))
			currNode = (SimpleNode) astFunction.jjtGetChild(++argumentsIndex);

		createStatementsHHIR((ASTSTATEMENTS) currNode, function);
	}

	private void createStatementsHHIR(ASTSTATEMENTS aststatements, IRMethod irmethod)
	{
		SimpleNode child = (SimpleNode) aststatements.jjtGetChild(0);

		switch (child.toString()) {
			case "ASSIGN":
				createAssignHHIR(child, irmethod);
				break;
			case "CALL":
				createCallHHIR(child, irmethod);
				break;
			default:
				System.out.println("Not generating HHIR for " + child.toString() + " yet.");
				break;
		}
	}

	/**
	 * Retrieves the name and may retrieve also type, value and operation for some variable
	 * @param child simplenode
	 * @param irmethod irmethod
	 */
	private void createAssignHHIR(SimpleNode child, IRMethod irmethod) {
		String name = null;
		Integer index = -1;
		Integer value = -1;
		Type type = null;
		Integer size = -1;

		ASTLHS astlhs = (ASTLHS) child.jjtGetChild(0);
		ASTRHS astrhs = (ASTRHS) child.jjtGetChild(1);

		SimpleNode lhchild = (SimpleNode) astlhs.jjtGetChild(0);
		switch(lhchild.toString()) {
			case "ARRAYACCESS":
				ASTARRAYACCESS astarrayaccess = (ASTARRAYACCESS) lhchild.jjtGetChild(0);
				/*if(astarrayaccess.jjtGetNumChildren() == 1) {
				//TODO: Index can also be a variable
					index = ((INDEX) astarrayaccess).jjtGetChild(0)
				}*/
				break;
			case "SCALARACCESS":
				ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) lhchild;
				name = astscalaraccess.id;
				break;
		}

		SimpleNode rhchild = (SimpleNode) astrhs.jjtGetChild(0);
		switch (rhchild.toString()) {
			case "TERM":
				ASTTERM term = (ASTTERM) rhchild;

				if(term.operator == null && term.integer!= null) {
					type = Type.INTEGER;
					value = term.integer;
				}
				else if(term.operator != null && term.integer!= null) {
					type = Type.INTEGER;
					String str_value = term.operator + term.integer;
					value = Integer.parseInt(str_value);
				}


				//TODO: Term call, arrayaccess and scalaraccess
				/*if(term.operator != null) {
					//Term has an operator
				}

				if(astrhs.jjtGetNumChildren() == 2) {
					ASTTERM newTerm = (ASTTERM) astrhs.jjtGetChild(1);
				}*/
				break;
			case "ARRAYSIZE":
				ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) rhchild;
				if(astarraysize.jjtGetNumChildren() == 0) {
					size = astarraysize.integer;
				}
				else {
					ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
					//TODO: Retrieve variable value
				}
				break;
		}

		//TODO: Debug
		System.out.println("\nname= " + name);
		System.out.println("type= " + type.toString());
		System.out.println("index= " + index);
		System.out.println("value= " + value + "\n");

		if(type == Type.INTEGER) {
			irmethod.addChild(new IRAllocate(name, type, value));
		}
	}

	private void createCallHHIR(SimpleNode child, IRMethod irmethod) {
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
					if(astarraysize.jjtGetNumChildren() == 0) {
						size = astarraysize.integer;
					}
					else {
						ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
						//TODO: Retrieve variable value
					}
				}
				else {
					String str_value = astdeclaration.operator + astdeclaration.integer;
					value = Integer.parseInt(str_value);
				}
				break;
		}

		switch (type) {
			case INTEGER:
				root.addChild(new IRGlobal(name, type, value));
				break;
			case ARRAY:
				root.addChild(new IRGlobal(name, type, value, size));
				break;
			default:
				System.err.println("Error on adding declaration to HHIR");
				System.exit(-1);
		}
	}
}
