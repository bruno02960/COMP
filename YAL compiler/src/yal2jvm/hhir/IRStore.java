package yal2jvm.hhir;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;
    protected boolean arrayAccess = false;
    protected IRLoad index = null;
    private int register;

    protected ArrayList<String> getInstForStoring(boolean arrayAccess, IRLoad index, IRNode value)
    {
        ArrayList<String> inst = new ArrayList<>();

        //check if it is one of the method's arguments
        register = ((IRMethod) parent).getArgumentRegister(name);

        //if not, check if storage variable exists, and if so get its register
        if (register == -1)
            register = ((IRMethod) parent).getVarRegister(name);

        //code for global
        if (register == -1)
        {
        	IRModule module = (IRModule)findParent("Module");
        	IRGlobal global = module.getGlobal(name);
        	String instruction = null;

        	if (global != null)
        	{
        	    switch (global.getType()) {
                    case INTEGER:
                        instruction = "putstatic " + module.getName() + "/" + name + " I";
                        inst.add(instruction);
                        break;
                    case VARIABLE:
                        instruction = "putstatic " + module.getName() + "/" + name + " I";
                        inst.add(instruction);
                        break;
                    case ARRAY:
                        inst.addAll(setGlobalArrayElementByIRNode(index, Type.ARRAY, name, value));
                }

        		return inst;
        	}
        }

        //if storage variable does not exist (locally or globally), allocate it
        if (register == -1)
        {
            IRAllocate storeVar = new IRAllocate(name, new Variable("0", Type.INTEGER));
            IRMethod method = (IRMethod) findParent("Method");
            method.addChild(storeVar);
            register = storeVar.getRegister();
        }

        if(arrayAccess)
        {
            inst.add("pop");
            inst.addAll(setLocalArrayElementByIRNode(index, register, value));
        }
        else
            inst.add(getInstructionToLoadIntFromRegisterToStack(register));

        return inst;
    }
}
