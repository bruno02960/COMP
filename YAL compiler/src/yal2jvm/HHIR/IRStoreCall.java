package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRStoreCall extends IRNode
{

    private String name;

    public IRStoreCall(String name, IRCall call)
    {
        this.name = name;
        this.nodeType = "StoreCall";
        addChild(call);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        return inst;
    }

}
