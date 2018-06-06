package yal2jvm.hlir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

/**
 * TODO
 */
public abstract class IRNode
{
	protected IRNode parent;
	protected ArrayList<IRNode> children;
	protected String nodeType;

	/**
	 *TODO
	 * @param irNode
	 */
	public IRNode(IRNode irNode)
	{
		this.children = new ArrayList<>(irNode.getChildren());
		this.parent = irNode.parent;
		this.nodeType = new String(irNode.getNodeType());
	}

	/**
	 *TODO
	 */
	public IRNode()
	{
		children = new ArrayList<>();
	}

	/**
	 * Adds a IRNode to the field children and makes this object its parent
	 * @param child	TODO
	 */
	public void addChild(IRNode child)
	{
		children.add(child);
		child.setParent(this);
	}

	/**
	 * Returns the value of the field parent
	 * @return value of the field parent
	 */
	public IRNode getParent()
	{
		return parent;
	}

	/**
	 * Sets the value of the field parent to the value of the parameter parent
	 * @param parent new value for the field parent
	 */
	public void setParent(IRNode parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns the value of the field children
	 * @return the value of the field children
	 */
	public ArrayList<IRNode> getChildren()
	{
		return children;
	}

	/**
	 * Sets the value of the field children to the value of the parameter children
	 * @param children	new value for the field children
	 */
	public void setChildren(ArrayList<IRNode> children)
	{
		this.children = children;
	}

	/**
	 * TODO
	 * @return
	 */
	public abstract ArrayList<String> getInstructions();

	/**
	 * TODO
	 * @param instruction
	 * @param registerNumber
	 * @return
	 */
	private String getInstructionLoadOrStoreInstructionMoreEfficient(String instruction, int registerNumber)
	{
		if (registerNumber < 4)
			return instruction + "_" + registerNumber;
		else
			return instruction + " " + registerNumber;
	}

	/**
	 * TODO
	 * @param registerNumber
	 * @return
	 */
	protected String getInstructionToLoadIntFromRegisterToStack(int registerNumber)
	{
		return getInstructionLoadOrStoreInstructionMoreEfficient("iload", registerNumber);
	}

	/**
	 * TODO
	 * @param registerNumber
	 * @return
	 */
	protected String getInstructionToStoreIntInRegister(int registerNumber)
	{
		return getInstructionLoadOrStoreInstructionMoreEfficient("istore", registerNumber);
	}

	/**
	 * TODO
	 * @param registerNumber
	 * @return
	 */
	protected String getInstructionToLoadArrayFromRegisterToStack(int registerNumber)
	{
		return getInstructionLoadOrStoreInstructionMoreEfficient("aload", registerNumber);
	}

	/**
	 * TODO
	 * @param registerNumber
	 * @return
	 */
	protected String getInstructionToStoreArrayInRegister(int registerNumber)
	{
		return getInstructionLoadOrStoreInstructionMoreEfficient("astore", registerNumber);
	}

	/**
	 * TODO
	 * @param type
	 * @param name
	 * @return
	 */
	protected String getInstructionToLoadGlobalToStack(Type type, String name)
	{
		String varType = type == Type.INTEGER ? "I" : "[I";
		return "getstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
	}

	/**
	 * TODO
	 * @param type
	 * @param name
	 * @return
	 */
	protected String getInstructionToStoreGlobal(Type type, String name)
	{
		String varType = type == Type.INTEGER ? "I" : "[I";
		return "putstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
	}

	/**
	 * Returns the value of the field nodeType
	 * @return	value of the field nodeType
	 */
	@Override
	public String toString()
	{
		return this.nodeType;
	}

	/**
	 * TODO
	 * @param nodeType
	 * @return
	 */
	public IRNode findParent(String nodeType)
	{
		IRNode res;
		IRNode par = this.parent;
		while (true)
		{
			if (par.toString().equals(nodeType))
			{
				res = par;
				break;
			} else
			{
				par = par.getParent();
				if (par == null)
				{
					return null;
				}
			}
		}

		return res;
	}

	/**
	 * TODO
	 * @param varName
	 * @return
	 */
	protected IRNode getVarIfExists(String varName)
	{
		IRModule module = (IRModule) findParent("Module");
		IRGlobal irGlobal = module.getGlobal(varName);
		if (irGlobal != null)
			return irGlobal;

		IRMethod method = (IRMethod) findParent("Method");
		int register = method.getArgumentRegister(varName);
		if (register != -1)
			return new IRArgument(register);

		ArrayList<IRNode> children = method.getChildren();
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).toString().equals("Allocate"))
			{
				IRAllocate alloc = (IRAllocate) children.get(i);
				if (alloc.getName().equals(varName))
				{
					alloc.getRegister();
					return alloc;
				}
			}
		}

		return null;
	}

	/**
	 * TODO
	 * @param index
	 * @param register
	 * @param value
	 * @return
	 */
	protected ArrayList<String> setLocalArrayElementByIRNode(IRNode index, int register, IRNode value)
	{
		String loadArrayRefInstruction = getInstructionToLoadArrayFromRegisterToStack(register);
		return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
	}

	/**
	 * TODO
	 * @param index
	 * @param type
	 * @param name
	 * @param value
	 * @return
	 */
	protected ArrayList<String> setGlobalArrayElementByIRNode(IRNode index, Type type, String name, IRNode value)
	{
		String loadArrayRefInstruction = getInstructionToLoadGlobalToStack(type, name);
		return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
	}

	/**
	 * TODO
	 * @param indexInstructions
	 * @param loadArrayRefInstruction
	 * @param value
	 * @return
	 */
	protected ArrayList<String> setArrayElement(ArrayList<String> indexInstructions, String loadArrayRefInstruction,
			IRNode value)
	{
		ArrayList<String> inst = new ArrayList<>();

		inst.add(loadArrayRefInstruction);
		inst.addAll(indexInstructions);
		inst.addAll(value.getInstructions());
		inst.add("iastore");

		return inst;
	}

	/**
	 * TODO
	 * @param name
	 * @param module
	 * @return
	 */
	protected String getGlobalVariableGetCode(String name, IRModule module)
	{
		IRGlobal global = module.getGlobal(name);
		if (global == null)
		{
			System.out.println("Internal error! The program will be closed.");
			System.exit(-1);
		}

		String in = "getstatic " + module.getName() + "/" + global.getName() + " ";
		in += global.getType() == Type.ARRAY ? "[I" : "I";

		return in;
	}

	/**
	 * TODO
	 * @param name
	 * @param method
	 * @return
	 */
	protected String getGlobalVariableGetCodeByIRMethod(String name, IRMethod method)
	{
		IRModule module = ((IRModule) method.getParent());
		return getGlobalVariableGetCode(name, module);
	}

	/**
	 *TODO
	 * @param arrayRefJVMCode
	 * @param valueJVMCode
	 * @return
	 */
	protected ArrayList<String> getCodeForSetAllArrayElements(String arrayRefJVMCode, ArrayList<String> valueJVMCode)
	{
		ArrayList<String> inst = new ArrayList<>();

		inst.add(arrayRefJVMCode);
		inst.add("arraylength");
		inst.add("init:");
		inst.add("iconst_1");
		inst.add("isub");
		inst.add("dup");
		inst.add("dup");
		inst.add("iflt end");
		inst.add(arrayRefJVMCode);
		inst.add("swap");
		inst.addAll(valueJVMCode);
		inst.add("iastore");
		inst.add("goto init");
		inst.add("end:");

		return inst;
	}

	/**
	 * Returns the value of the field nodeType
	 * @return	value of the field nodeType
	 */
	public String getNodeType()
	{
		return nodeType;
	}

	/**
	 * Sets the value of the field nodeType to the value of the parameter nodeType
	 * @param nodeType new value for the field nodeType
	 */
	public void setNodeType(String nodeType)
	{
		this.nodeType = nodeType;
	}

	/**
	 * TODO
	 * @param name
	 * @param index
	 */
	public String getVarNameForConstantName(String name, IRNode index)
	{
		String varName = name; // not array access, so integer
		if (index != null && index instanceof IRConstant) // array access then
			varName = name + "-" + ((IRConstant) index).getValue();

		return varName;
	}
}
