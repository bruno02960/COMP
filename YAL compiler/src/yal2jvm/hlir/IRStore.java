package yal2jvm.hlir;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;
    protected boolean arrayAccess = false;
    protected IRLoad index = null;
    private int register;

    protected ArrayList<String> getInstForStoring(boolean arrayAccess, IRLoad index, IRNode value)
    {
        //check if it is one of the method's arguments
        register = ((IRMethod) parent).getArgumentRegister(name);

        //if not, check if storage variable exists, and if so get its register
        if (register == -1)
            register = ((IRMethod) parent).getVarRegister(name);

        IRModule module = (IRModule)findParent("Module");
        //code for check global
        if (register == -1)
        {
            IRGlobal global = module.getGlobal(name);
        	if (global != null)
                return getInstForStoringGlobalVariable(index, value, module, global);
        }
        else
            module.removeAllocateChild(name);

        //if storage variable does not exist (locally or globally), allocate it
        if (register == -1)
        {
            IRAllocate storeVar = new IRAllocate(name, new Variable("0", Type.INTEGER));
            IRMethod method = (IRMethod) findParent("Method");
            method.addChild(storeVar);
            register = storeVar.getRegister();
        }

        return getInstForStoringLocalVariable(arrayAccess, index, value);
    }

    private ArrayList<String> getInstForStoringLocalVariable(boolean arrayAccess, IRLoad index, IRNode value)
    {
        ArrayList<String> inst = new ArrayList<>();
        if(arrayAccess)
        {
            inst.add("pop");
            inst.addAll(setLocalArrayElementByIRNode(index, register, value));
        }
        else
        {
            if(value instanceof IRCall && ((IRCall) value).getType().equals(Type.ARRAY))
                inst.add(getInstructionToStoreArrayInRegister(register));
            else
                inst.add(getInstructionToStoreIntInRegister(register));
        }

        return inst;
    }

    private ArrayList<String> getInstForStoringGlobalVariable(IRLoad index, IRNode value, IRModule module, IRGlobal global)
    {
        ArrayList<String> inst = new ArrayList<>();
        if(global.getType() == Type.ARRAY)
            inst.addAll(setGlobalArrayElementByIRNode(index, Type.ARRAY, name, value));
        else
        {
            //Type = Integer or type = Variable
            String instruction = "putstatic " + module.getName() + "/" + name + " I";
            inst.add(instruction);
        }

        return inst;
    }
}
