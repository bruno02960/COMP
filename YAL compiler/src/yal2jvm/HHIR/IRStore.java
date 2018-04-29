package yal2jvm.HHIR;

import java.util.ArrayList;

public abstract class IRStore extends IRNode
{
    protected String name;

    protected ArrayList<String> getInstForStoring()
    {
        ArrayList<String> inst = new ArrayList<>();

        switch (parent.toString())
        {
            case "Method":
            {
                int storeReg = -1;
                ArrayList<IRNode> methodChildren = parent.getChildren();

                //check if storage variable exists, and if so get its register
                storeReg = ((IRMethod) parent).getVarRegister(name);

                //if not, check if it is one of the method's arguments
                if (storeReg == -1)
                    storeReg = ((IRMethod) parent).getArgumentRegister(name);

                //code for global
                //if storage variable does not exist, allocate it
                if (storeReg == -1)
                {
                    IRAllocate storeVar = new IRAllocate(name, Type.INTEGER, 0);
                    parent.addChild(storeVar);
                    storeReg = storeVar.getRegister();
                }

                inst.add("istore " + storeReg);
                break;
            }
            default:
                break;
        }

        return inst;
    }
}
