package yal2jvm.hhir;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;
    protected boolean arrayAccess = false;
    protected IRLoad index = null;
    private int register;

    protected ArrayList<String> getInstForStoring(boolean arrayAccess, IRLoad index)
    {
        ArrayList<String> inst = new ArrayList<>();

        //check if storage variable exists, and if so get its register
        register = ((IRMethod) parent).getVarRegister(name);

        //if not, check if it is one of the method's arguments
        if (register == -1)
            register = ((IRMethod) parent).getArgumentRegister(name);

        //code for global
        if (register == -1)
        {
        	IRModule module = (IRModule)findParent("Module");
        	IRGlobal global = module.getGlobal(name);
        	if (global != null)
        	{
        		String type = global.getType() == Type.INTEGER ? "I" : "A";
        		String moduleName = module.getName();
        		
        		String storeInst = "putstatic " + moduleName + "/" + name + " " + type;
        		inst.add(storeInst);
        		return inst;
        	}
        }
        //if storage variable does not exist, allocate it
        if (register == -1)
        {
            IRAllocate storeVar = new IRAllocate(name, new Variable("0", Type.INTEGER));
            parent.addChild(storeVar);
            register = storeVar.getRegister();
        }

        if(arrayAccess)
            inst.addAll(setArrayElement(index, register));
        else
            inst.add(getInstructionToStoreRegisterToStack(register));

        return inst;
    }




    protected String getInstructionToStoreRegisterToStack(int registerNumber)
    {
        if(registerNumber < 4)
            return "istore_" + registerNumber;
        else
            return "istore " + registerNumber;
    }
}
