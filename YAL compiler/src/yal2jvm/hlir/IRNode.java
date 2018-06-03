package yal2jvm.hlir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

public abstract class IRNode
{
    protected IRNode parent;
    protected ArrayList<IRNode> children;
    protected String nodeType;

    public IRNode(IRNode irNode)
    {
        this.children = new ArrayList<>(irNode.getChildren());
        this.parent = irNode.parent;
        this.nodeType = new String(irNode.getNodeType());
    }

    public IRNode()
    {
        children = new ArrayList<>();
    }

    public void addChild(IRNode child)
    {
        children.add(child);
        child.setParent(this);
    }

    public IRNode getParent()
    {
        return parent;
    }

    public void setParent(IRNode parent)
    {
        this.parent = parent;
    }

    public ArrayList<IRNode> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<IRNode> children)
    {
        this.children = children;
    }

    public abstract ArrayList<String> getInstructions();

    private String getInstructionLoadOrStoreInstructionMoreEfficient(String instruction, int registerNumber)
    {
        if(registerNumber < 4)
            return instruction + "_" + registerNumber;
        else
            return instruction + " " + registerNumber;
    }

    protected String getInstructionToLoadIntFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("iload", registerNumber);
    }

    protected String getInstructionToStoreIntInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("istore", registerNumber);
    }

    protected String getInstructionToLoadArrayFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("aload", registerNumber);
    }

    protected String getInstructionToStoreArrayInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("astore", registerNumber);
    }

    protected String getInstructionToLoadGlobalToStack(Type type, String name)
    {
        String varType = type == Type.INTEGER ? "I" : "[I";
        return "getstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
    }

    protected String getInstructionToStoreGlobal(Type type, String name)
    {
        String varType = type == Type.INTEGER ? "I" : "[I";
        return "putstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
    }

    @Override
    public String toString()
    {
        return this.nodeType;
    }
    
    public IRNode findParent(String nodeType)
    {
        IRNode res = null;
        IRNode par = this.parent;
        while (true)
        {
            if (par.toString().equals(nodeType))
            {
                res = par;
                break;
            } 
            else
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

    protected IRNode getVarIfExists(String varName)
    {
        IRModule module = (IRModule) findParent("Module");
        IRGlobal irGlobal = module.getGlobal(varName);
        if(irGlobal != null)
            return irGlobal;

        IRMethod method = (IRMethod) findParent("Method");
        int register = method.getArgumentRegister(varName);
        if(register != -1)
            return new IRArgument(register);

        ArrayList<IRNode> children = method.getChildren();
        for (int i = 0; i < children.size(); i++)
        {
            if (children.get(i).toString().equals("Allocate"))
            {
                IRAllocate alloc = (IRAllocate)children.get(i);
                if (alloc.getName().equals(varName))
                {
                    alloc.getRegister();
                    return alloc;
                }
            }
        }

        return null;
    }

    protected ArrayList<String> setLocalArrayElementByIRNode(IRNode index, int register, IRNode value)
    {
        String loadArrayRefInstruction = getInstructionToLoadArrayFromRegisterToStack(register);
        return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
    }

    protected ArrayList<String> setGlobalArrayElementByIRNode(IRNode index, Type type, String name, IRNode value)
    {
        String loadArrayRefInstruction = getInstructionToLoadGlobalToStack(type, name);
        return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
    }

    protected ArrayList<String> setArrayElement(ArrayList<String> indexInstructions, String loadArrayRefInstruction, IRNode value)
    {
        ArrayList<String> inst = new ArrayList<>();

        inst.add(loadArrayRefInstruction);
        inst.addAll(indexInstructions);
        inst.addAll(value.getInstructions());
        inst.add("iastore");

        return inst;
    }

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

    protected String getGlobalVariableGetCodeByIRMethod(String name, IRMethod method)
    {
        IRModule module = ((IRModule) method.getParent());
        return getGlobalVariableGetCode(name, module);
    }

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

	public String getNodeType()
	{
		return nodeType;
	}

	public void setNodeType(String nodeType)
	{
		this.nodeType = nodeType;
	}

}
