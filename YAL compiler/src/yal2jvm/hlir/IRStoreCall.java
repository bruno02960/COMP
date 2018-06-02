package yal2jvm.hlir;

import java.util.ArrayList;

public class IRStoreCall extends IRStore
{
    public IRStoreCall()
    {
        this.setNodeType("StoreCall");
    }

    // a = f();
    public IRStoreCall(String name)
    {
        this();
        this.name = name;
    }

    // a[i] = f();
    public IRStoreCall(VariableArray name)
    {
        this();
        this.name = name.getVar();
        this.arrayAccess = true;
        Variable at = name.getAt();
        if(at.getType().equals(Type.INTEGER))
            this.index = new IRConstant(at.getVar());
        else
            this.index = new IRLoad(at);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<IRNode> childs = getChildren(); //one and only one child, an IRCall
        IRCall irCall = (IRCall) childs.get(0);
        inst.addAll(irCall.getInstructions());
        inst.addAll(getInstForStoring(arrayAccess, index, irCall));

        return inst;
    }

}
