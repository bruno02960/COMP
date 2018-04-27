package yal2jvm.HHIR;

import java.util.ArrayList;

import yal2jvm.ast.*;

public class HHIR
{
	private IRModule root;
	private SimpleNode ast;

	//TODO: Debug
	boolean declarationDebug = true;
	boolean functionDebug = true;
	boolean assignDebug = true;
	
	public HHIR(SimpleNode ast)
	{
		this.ast = ast;
		this.root = createHHIR();
		
		//hardcoded
		//this.root = createHardcoded();
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
		Type returnType = Type.VOID;
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

		if(functionDebug) {
			System.out.println("name= " + functionId);
			System.out.println("return type= " + returnType.toString());
			if (returnName != null)
				System.out.println("return name= " + returnName);
			if (argumentsTypes != null) {
				System.out.println("argumentsTypes= ");
				for (Type argumentsType : argumentsTypes) System.out.println(argumentsType);
			}

			if (argumentsNames != null) {
				System.out.println("argumentsNames= ");
				for (String argumentsName : argumentsNames) System.out.println(argumentsName);
			}
		}

		root.addChild(function);

		if(!(currNode instanceof ASTSTATEMENTS))
			currNode = (SimpleNode) astFunction.jjtGetChild(++argumentsIndex);

			createStatementsHHIR((ASTSTATEMENTS) currNode, function);
	}

	private void createStatementsHHIR(ASTSTATEMENTS aststatements, IRMethod irmethod)
	{
		for(int i=0; i<aststatements.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) aststatements.jjtGetChild(i);

			switch (child.toString()) {
				case "ASSIGN":
					createAssignHHIR(child, irmethod);
					break;
				case "CALL":
					createCallHHIR((ASTCALL) child, irmethod);
					break;
				default:
					System.out.println("Not generating HHIR for " + child.toString() + " yet.");
					break;
			}
		}
	}

	/**
	 * Retrieves the name and may retrieve also type, value and operation for some variable
	 * @param child simplenode
	 * @param irmethod irmethod
	 */
	private void createAssignHHIR(SimpleNode child, IRMethod irmethod) {
		String name = null;
		Integer value = -1;
		Type type = null;
		String size = null;
		String operator;

		ASTLHS astlhs = (ASTLHS) child.jjtGetChild(0);
		ASTRHS astrhs = (ASTRHS) child.jjtGetChild(1);

		ArrayList<String> operands = new ArrayList<>();

		SimpleNode lhchild = (SimpleNode) astlhs.jjtGetChild(0);
		switch(lhchild.toString()) {
			case "ARRAYACCESS":
				ASTARRAYACCESS astarrayaccess = (ASTARRAYACCESS) lhchild;
				ASTINDEX astindex = (ASTINDEX) astarrayaccess.jjtGetChild(0);

				if(astindex.indexID != null) {
					name = astarrayaccess.arrayID + "[" + astindex.indexID + "]";
				}
				else {
					name = astarrayaccess.arrayID + "[" + astindex.indexValue + "]";
				}
				break;
			case "SCALARACCESS":
				ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) lhchild;
				name = astscalaraccess.id;
				break;
		}

		operator=astrhs.operator;
		int numChildren = astrhs.jjtGetNumChildren();
		for(int i=0; i<numChildren; i++) {
			SimpleNode rhchild = (SimpleNode) astrhs.jjtGetChild(i);
			switch (rhchild.toString()) {
				case "TERM":
					ASTTERM term = (ASTTERM) rhchild;

					if(term.integer != null) {
						type = Type.INTEGER;
						String str_value = term.operator + term.integer;
						value = Integer.parseInt(str_value);
						operands.add(str_value);
					}

					if (term.jjtGetNumChildren() == 1) {
						SimpleNode termChild = (SimpleNode) term.jjtGetChild(0);

						switch (termChild.toString()) {
							case "CALL":
								//TODO: How to proceed in this case?
								break;
							case "ARRAYACCESS":
								ASTARRAYACCESS astarrayaccess = ((ASTARRAYACCESS)termChild);
								ASTINDEX astindex = (ASTINDEX) termChild.jjtGetChild(0);
								String arrayaccess = term.operator + astarrayaccess.arrayID + "[" + (astindex.indexID!=null ? astindex.indexID : astindex.indexValue.toString()) + "]";
								operands.add(arrayaccess);
								break;
							case "SCALARACCESS":
								operands.add(term.operator + ((ASTSCALARACCESS) termChild).id);
								break;
						}
					}
					break;
				case "ARRAYSIZE":
					ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) rhchild;

					if (astarraysize.jjtGetNumChildren() == 0) {
						size = astarraysize.integer.toString();
					} else {
						ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
						size = astscalaraccess.id;
					}
					break;
			}
		}

		//TODO: Debug
		if(assignDebug) {
			System.out.println();
			System.out.println(name!=null ? "name = " + name : "null");
			for (String operand: operands) {
				System.out.println("operand = " + operand);
			}
			System.out.println(!operator.equals("") ? "operator = " + operator : "null");
			System.out.println(size!=null ? "size = " + size : "null");
			System.out.println();
		}

		if(type == Type.INTEGER) {
			irmethod.addChild(new IRAllocate(name, type, value));
		}
	}

	private void createCallHHIR(ASTCALL astCall, IRNode irNode)
	{
		String moduleId = astCall.module;
		String methodId = astCall.method;
		ArrayList<PairStringType> arguments = null;

		if(astCall.jjtGetNumChildren() > 0)
		{
			ASTARGUMENTS astarguments = (ASTARGUMENTS) astCall.jjtGetChild(0);
			arguments = getFunctionCallArgumentsIds(astarguments);
		}

		IRCall irCall = new IRCall(methodId, moduleId, arguments);

		//TODO debug
        System.out.println("from createCallHHIR");
        System.out.println("moduleId= " + moduleId);
        System.out.println("methodId= " + methodId);

        if (arguments != null) {
            System.out.println("arguments");
			for (PairStringType argument : arguments)
				System.out.println("value: " + argument.getString() +
						"   type: " + argument.getType().toString());
        }

		irNode.addChild(irCall);
	}

	private ArrayList<PairStringType> getFunctionCallArgumentsIds(ASTARGUMENTS astArguments)
	{
		ArrayList<PairStringType> arguments = new ArrayList<>();
		int numArguments = astArguments.jjtGetNumChildren();
		for(int i = 0; i < numArguments; i++)
		{
			ASTARGUMENT astArgument = (ASTARGUMENT) astArguments.jjtGetChild(i);
			if(astArgument.intArg != null)
			{
				PairStringType pair = new PairStringType(astArgument.intArg.toString(), Type.INTEGER);
				arguments.add(pair);
				continue;
			}
			if(astArgument.stringArg != null)
			{
				PairStringType pair = new PairStringType(astArgument.stringArg, Type.STRING);
				arguments.add(pair);
				continue;
			}
			if(astArgument.idArg != null)
			{
				PairStringType pair = new PairStringType(astArgument.idArg, Type.VARIABLE);
				arguments.add(pair);
			}
		}

		return arguments;
	}

	/**
     * Retrieves the name, type and value of the variable
     * @param astdeclaration declaration to analyse
     */
	private void createDeclarationHHIR(ASTDECLARATION astdeclaration) {

	    SimpleNode simpleNode = (SimpleNode) astdeclaration.jjtGetChild(0);

	    String name = null;
	    Type type = null;
	    String variable = null;
	    boolean isSize = false;
	    int value = -1;
	    int size = -1;

	    switch (simpleNode.toString()) {
			case "SCALARELEMENT":
				ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) simpleNode;
				name = astscalarelement.id;

				if(astdeclaration.jjtGetNumChildren() == 2) {
					ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);

					if(astarraysize.jjtGetNumChildren() == 0) {
						size = astarraysize.integer;
					}
					else {
						ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
						variable = astdeclaration.operator + astscalaraccess.id;

						if(variable.contains(".size")) {
							isSize = true;
						}
					}

					type = Type.ARRAY;
				}
				else {
					type = Type.INTEGER;
					String str_value = astdeclaration.operator + astdeclaration.integer;
					if(!str_value.equals("null"))
						value = Integer.parseInt(str_value);
				}
				break;
			case "ARRAYELEMENT":
				ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) simpleNode;
				name = astarrayelement.id;
				type = Type.ARRAY;

				if(astdeclaration.jjtGetNumChildren() == 2) {
					ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);
					if(astarraysize.jjtGetNumChildren() == 0) {
						size = astarraysize.integer;
					}
					else {
						ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
						variable = astscalaraccess.id;

						if(variable.contains(".size")) {
							isSize = true;
						}
					}
				}
				else {
					String str_value = astdeclaration.operator + astdeclaration.integer;
					if(!str_value.equals("null"))
						value = Integer.parseInt(str_value);
				}
				break;
		}

		if(declarationDebug) {
			System.out.println();
			System.out.println(name!=null ? "name = " + name : "null");
			System.out.println(type!=null ? "type = " + type : "null");
			System.out.println(isSize ? "isSize = true" : "isSize = false");
			System.out.println(variable!=null ? "variable = " + variable : "null");
			System.out.println(value!=-1 ? "value = " + value : "null");
			System.out.println(size!=-1 ? "size = " + size : "null");
			System.out.println();
		}

		assert type != null;
		switch (type) {
			case INTEGER:
				root.addChild(new IRGlobal(name, type, value));
				break;
			case ARRAY:
				root.addChild(new IRGlobal(name, type, value, size));
				break;
		}
	}
}
