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
        switch (type)
        {
            case VOID:
                inst.add("return");
                break;

            case INTEGER:
                IRLoad irLoad = new IRLoad(name);
                addChild(irLoad);
                inst.addAll(irLoad.getInstructions());
                inst.add("ireturn");
                break;

            case ARRAY:
                //TODO
                System.out.println("Not generating HHIR for return value ARRAY yet.");
        }

        return inst;
    }

}
