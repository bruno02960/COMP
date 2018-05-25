package yal2jvm.hhir;

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
        if(type == Type.VOID)
            inst.add("return");
        else
        {
            IRLoad irLoad = new IRLoad(name, type);
            addChild(irLoad);
            inst.addAll(irLoad.getInstructions());
            if(type == Type.ARRAY)
                inst.add("areturn");
            else
                inst.add("ireturn");
        }

        return inst;
    }

}
