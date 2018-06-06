package yal2jvm.hlir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

/**
 *
 */
public class IRGlobal extends IRNode
{
	private String name;
	private Type type;
	private Variable value = null;
	private boolean arraySize = false;
	private ArrayList<String> staticArraysInstructions = new ArrayList<>();

	/**
	 * Constructor for cases when we have only declaration of a global variable,
	 * integer or array. Cases like a[]; or a;
	 * 
	 * @param variable
	 *            the variable being declared
	 */
	public IRGlobal(Variable variable) //
	{
		this.name = variable.getVar();
		this.type = variable.getType();
		this.setNodeType("Global");
	}

	/**
	 * Constructor for cases when we have definition after declaration, or just
	 * definition of a global variable, integer or array.
	 * 
	 * @param variable
	 *            the variable being declared
	 * @param value
	 *            the value to be set in the variable
	 */
	public IRGlobal(Variable variable, Variable value)
	{
		this(variable);
		this.value = value;
	}

	/**
	 * Constructor for cases when we have a declaration of an array with a given
	 * size. Cases like a[]=[50]; or a=[50];
	 * 
	 * @param variable
	 *            the array whose size is being set
	 * @param value
	 *            the value of the array size
	 * @param arraySize
	 *            the Type.ARRAYSIZE, it's mandatory
	 */
	public IRGlobal(Variable variable, Variable value, Type arraySize)
	{
		this(variable, value);
		assert arraySize == Type.ARRAYSIZE;
		this.arraySize = true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		if (value == null)
		{
			if (type == Type.ARRAY) // a[];
				return createGlobalArrayWithSize0();
			else // a;
				return createGlobalInteger();
		} else
		{
			if (type == Type.ARRAY) // a[] = ...
			{
				if (arraySize) // a[] = [50];
					return createGlobalArrayWithSize(value);
				else // a[] = 50;
				{
					assignAllArrayElements(value);
					return new ArrayList<>();
				}
			} else // a = ...
			{
				if (arraySize) // a = [50];
					return createGlobalArrayWithSize(value);
				else // a = 50;
					return createGlobalInteger();
			}
		}
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<String> createGlobalInteger()
	{
		ArrayList<String> insts = new ArrayList<>();
		String inst = ".field public static " + name;
		inst += " I = " + (value != null ? value.getVar() : 0);
		insts.add(inst);

		return insts;
	}

	/**
	 *
	 * @param sizeInstructions
	 * @return
	 */
	private ArrayList<String> createGlobalArray(ArrayList<String> sizeInstructions)
	{
		// declare array as global
		ArrayList<String> insts = new ArrayList<>();
		String inst = ".field public static " + name + " [I";
		insts.add(inst);

		// instructions to static init method
		staticArraysInstructions.addAll(sizeInstructions);
		staticArraysInstructions.add("newarray int");
		staticArraysInstructions.add("putstatic " + Yal2jvm.moduleName + "/" + name + " [I");

		return insts;
	}

	/**
	 *
	 * @return
	 */
	private ArrayList<String> createGlobalArrayWithSize0()
	{
		ArrayList<String> sizeInstructions = new ArrayList<>();
		sizeInstructions.add("iconst_0");

		return createGlobalArray(sizeInstructions);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	private ArrayList<String> createGlobalArrayWithSize(Variable value)
	{
		IRNode valueNode = getValueIRNode(value);
		return createGlobalArray(valueNode.getInstructions());
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	private IRNode getValueIRNode(Variable value)
	{
		IRNode valueNode;
		if (value.getType() == Type.INTEGER)
			valueNode = new IRConstant(value.getVar());
		else
			valueNode = new IRLoad(value);
		this.addChild(valueNode);
		return valueNode;
	}

	/**
	 *
	 * @param value
	 */
	private void assignAllArrayElements(Variable value)
	{
		IRNode valueNode = getValueIRNode(value);
		IRModule module = (IRModule) findParent("Module");
		String globalVariableJVMCode = getGlobalVariableGetCode(name, module);

		staticArraysInstructions
				.addAll(getCodeForSetAllArrayElements(globalVariableJVMCode, valueNode.getInstructions()));
	}

	/**
	 *
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *
	 * @return
	 */
	public Type getType()
	{
		return this.type;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<String> getStaticArraysInstructions()
	{
		return staticArraysInstructions;
	}

}
