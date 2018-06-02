package yal2jvm.hlir;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;
    protected boolean arrayAccess = false;
    protected IRNode index = null;
    private int register;

    protected ArrayList<String> getInstForStoring(boolean arrayAccess, IRNode index, IRNode value)
    {
        IRMethod method = (IRMethod) parent;

        //check if it is one of the method's arguments
        register = method.getArgumentRegister(name);

        //if not, check if storage variable exists, and if so get its register
        if (register == -1)
        {
            IRAllocate var = ((IRMethod) parent).getVarDeclaredUntilThis(name, this);
            if (var != null)
                register = var.getRegister();
            addVariableToConstIfAppropriated(value, method);
        }

        //code for check global
        IRModule module = (IRModule)findParent("Module");
        if (register == -1)
        {
            IRGlobal global = module.getGlobal(name);
            if (global != null)
                return getInstForStoringGlobalVariable(index, value, module, global);
        }


        //if storage variable does not exist (locally or globally), allocate it
        if (register == -1)
        {
            IRAllocate irAllocate = new IRAllocate(name, new Variable("0", Type.INTEGER));
            method.addNewChildAfterChild(this, irAllocate);
            register = irAllocate.getRegister();
            addVariableToConstIfAppropriated(value, method);
        }


        return getInstForStoringLocalVariable(arrayAccess, index, value);
    }

    private void addVariableToConstIfAppropriated(IRNode value, IRMethod method)
    {
        if(value instanceof IRArith)
        {
            String valueString = ((IRArith) value).getStringValueIfBothConstant();
            if(valueString != null)
                method.addToConstVarNameToConstValue(name, new IRConstant(valueString));
        }
    }

    private ArrayList<String> getInstForStoringLocalVariable(boolean arrayAccess, IRNode index, IRNode value)
    {
        ArrayList<String> inst = new ArrayList<>();
        if(arrayAccess)
        {
            //inst.add("pop");//TODO VER se nao causa problemas, assim evita o pop depois
            inst.addAll(setLocalArrayElementByIRNode(index, register, value));
        }
        else
        {
            inst.addAll(value.getInstructions());
            if(value instanceof IRCall && ((IRCall) value).getType().equals(Type.ARRAY))
                inst.add(getInstructionToStoreArrayInRegister(register));
            else
                inst.add(getInstructionToStoreIntInRegister(register));
        }

        return inst;
    }

    private ArrayList<String> getInstForStoringGlobalVariable(IRNode index, IRNode value, IRModule module, IRGlobal global)
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
