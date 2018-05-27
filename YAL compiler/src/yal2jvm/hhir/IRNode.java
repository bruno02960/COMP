package yal2jvm.hhir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

public abstract class IRNode
{
    protected IRNode parent;
    protected ArrayList<IRNode> children;
    protected String nodeType;

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

    String getInstructionToLoadIntFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("iload", registerNumber);
    }

    String getInstructionToStoreIntInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("istore", registerNumber);
    }

    String getInstructionToLoadArrayFromRegisterToStack(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("aload", registerNumber);
    }

    String getInstructionToStoreArrayInRegister(int registerNumber)
    {
        return getInstructionLoadOrStoreInstructionMoreEfficient("astore", registerNumber);
    }

    String getInstructionToLoadGlobalArrayToStack(Type type, String name)
    {
        String varType = type == Type.INTEGER ? "I" : "[I";
        return "getstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
    }

    String getInstructionToStoreGlobalArray(Type type, String name)
    {
        String varType = type == Type.INTEGER ? "I" : "[I";
        return "putstatic " + Yal2jvm.moduleName + "/" + name + " " + varType;
    }

    @Override
    public String toString()
    {
        return this.nodeType;
    }
    
    IRNode findParent(String nodeType)
    {
        IRNode res;
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

    IRNode getVarIfExists(String varName)
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
        for (IRNode aChildren : children) {
            if (aChildren.toString().equals("Allocate")) {
                IRAllocate alloc = (IRAllocate) aChildren;
                if (alloc.getName().equals(varName) && alloc.getRegister() != -1)
                    return alloc;
            }
        }

        return null;
    }

    ArrayList<String> setLocalArrayElementByIRNode(IRNode index, int register, IRNode value)
    {
        String loadArrayRefInstruction = getInstructionToLoadArrayFromRegisterToStack(register);
        return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
    }

    ArrayList<String> setGlobalArrayElementByIRNode(IRNode index, Type type, String name, IRNode value)
    {
        String loadArrayRefInstruction = getInstructionToLoadGlobalArrayToStack(type, name);
        return setArrayElement(index.getInstructions(), loadArrayRefInstruction, value);
    }

    private ArrayList<String> setArrayElement(ArrayList<String> indexInstructions, String loadArrayRefInstruction, IRNode value)
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add(loadArrayRefInstruction);
        inst.addAll(indexInstructions);
        inst.addAll(value.getInstructions());
        inst.add("iastore");

        return inst;
    }

    String getGlobalVariableGetCode(String name, IRMethod method)
    {
        IRModule module = ((IRModule) method.getParent());
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

    ArrayList<String> getCodeForSetAllArrayElements(String arrayRefJVMCode,
                                                    ArrayList<String> valueJVMCode)
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

        /*
            aload_1
            arraylength
            init:
            iconst_1
            isub
            dup
            dup
            iflt end
            aload_1
            swap
            aload_1
            arraylength
            iastore
            goto init
            end:
         */


        return inst;
    }

}
