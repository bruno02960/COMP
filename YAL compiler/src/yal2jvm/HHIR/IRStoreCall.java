package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRStoreCall extends IRNode
{
    private String name;

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



        return inst;
    }

}
