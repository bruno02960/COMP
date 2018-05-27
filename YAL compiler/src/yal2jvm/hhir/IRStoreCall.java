package yal2jvm.hhir;

import java.util.ArrayList;

public class IRStoreCall extends IRStore
{
    // a = f();
    IRStoreCall(String name)
    {
        this.name = name;
        this.nodeType = "StoreCall";
    }

    // a[i] = f();
    IRStoreCall(VariableArray name)
    {
        this.name = name.getVar();
        this.nodeType = "StoreCall";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<IRNode> childs = getChildren(); //one and only one child, an IRCall
        IRCall irCall = (IRCall) childs.get(0);
        inst.addAll(irCall.getInstructions());
        inst.addAll(getInstForStoring(false, null, irCall));

        return inst;
    }

}
