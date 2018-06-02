package yal2jvm.hlir;

import java.util.ArrayList;

public class IRStoreCall extends IRStore
{
    // a = f();
    public IRStoreCall(String name)
    {
        this.setName(name);
        this.setNodeType("StoreCall");
    }

    // a[i] = f();
    public IRStoreCall(VariableArray name)
    {
        this.setName(name.getVar());
        this.setNodeType("StoreCall");
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
