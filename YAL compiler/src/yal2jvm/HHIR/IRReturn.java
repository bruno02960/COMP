package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRReturn extends IRNode
{

    private String name;
    private Type type;

    public IRReturn(String name, Type type)
    {
        this.name = name;
        this.type = type;
        this.nodeType = "Return";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.add("return");
        return inst;
    }

}
