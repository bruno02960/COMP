package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRStoreCall extends IRStore
{
    public IRStoreCall(String name)
    {
        this.name = name;
        this.nodeType = "StoreCall";
    }

    public IRStoreCall(String name, String index) //for arrays
    {
        this.name = name;
        this.nodeType = "StoreCall";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<IRNode> childs = getChildren(); //one and only one child, an IRCall
        IRCall irCall = (IRCall) childs.get(0);
        inst.addAll(irCall.getInstructions());
        inst.addAll(getInstForStoring());

        return inst;
    }

}
