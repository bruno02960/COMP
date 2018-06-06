package yal2jvm.hlir;

import java.util.ArrayList;

/**
 * TODO
 */
public class IRStoreCall extends IRStore
{
	/**
	 * Default constructor of IRStoreCall used by other constructors of the class
	 * that has basic and common action, setNodeType.
	 */
	public IRStoreCall()
	{
		this.setNodeType("StoreCall");
	}

	/**
	 * Constructor used for store call when lhs (the variable being set) has type
	 * integer. Example: a = f();
	 * 
	 * @param name
	 *            name of the variable where call will be stored
	 */
	public IRStoreCall(String name)
	{
		this();
		this.name = name;
	}

	/**
	 * Constructor used for store call when lhs (the variable being set) has type
	 * array. Value must be set at given index of the array. Example: a[i] = f();
	 * 
	 * @param name
	 *            name of the variable where call will be stored
	 */
	public IRStoreCall(VariableArray name)
	{
		this();
		this.name = name.getVar();
		this.arrayAccess = true;
		Variable at = name.getAt();
		if (at.getType().equals(Type.INTEGER))
			this.index = new IRConstant(at.getVar());
		else
			this.index = new IRLoad(at);
	}

	/**
	 * TODO
	 * @return
	 */
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();

		ArrayList<IRNode> childs = getChildren(); // one and only one child, an IRCall
		IRCall irCall = (IRCall) childs.get(0);
		// inst.addAll(irCall.getInstructions()); //TODO VER se nao causa problemas,
		// assim evita o pop depois
		inst.addAll(getInstForStoring(arrayAccess, index, irCall));

		return inst;
	}

}
