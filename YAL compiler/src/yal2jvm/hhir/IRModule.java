package yal2jvm.hhir;

import java.util.ArrayList;

public class IRModule extends IRNode
{
    private String name;

    public IRModule(String name)
    {
        super();
        this.setName(name);
        this.nodeType = "Module";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String inst1 = ".class public static " + name;
        String inst2 = ".super java/lang/Object";

        inst.add(inst1);
        inst.add(inst2);
        inst.add("\n");

        for (int i = 0; i < getChildren().size(); i++)
        {
            if (getChildren().get(i).toString().equals("Method"))
                inst.add("\n");
            inst.addAll(getChildren().get(i).getInstructions());
        }

        return inst;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public IRGlobal getGlobal(String name)
    {
        for (int i = 0; i < children.size(); i++)
        {
            if (children.get(i).toString().equals("Global"))
            {
                IRGlobal global = ((IRGlobal) children.get(i));
                if (global.getName().equals(name))
                    return global;
            }
        }
        return null;
    }
}
