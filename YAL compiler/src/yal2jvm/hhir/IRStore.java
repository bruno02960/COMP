package yal2jvm.hhir;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;

    protected ArrayList<String> getInstForStoring()
    {
        ArrayList<String> inst = new ArrayList<>();

        //check if storage variable exists, and if so get its register
        int storeReg = ((IRMethod) parent).getVarRegister(name);

        //if not, check if it is one of the method's arguments
        if (storeReg == -1)
            storeReg = ((IRMethod) parent).getArgumentRegister(name);

        //code for global
        if (storeReg == -1)
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
        if (storeReg == -1)
        {
            IRAllocate storeVar = new IRAllocate(name, Type.INTEGER, 0);
            parent.addChild(storeVar);
            storeReg = storeVar.getRegister();
        }

        inst.add(getInstructionToStoreRegisterToStack(storeReg));

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
