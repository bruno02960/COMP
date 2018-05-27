package yal2jvm.hhir;

import java.util.ArrayList;

public class IRModule extends IRNode
{
    private String name;
    private int currLabelNumber = 1;

    IRModule(String name)
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

    public int getAndIncrementCurrLabelNumber()
    {
        return currLabelNumber++;
    }

    public IRGlobal getGlobal(String name)
    {
        for (IRNode aChildren : children) {
            if (aChildren.toString().equals("Global")) {
                IRGlobal global = ((IRGlobal) aChildren);
                if (global.getName().equals(name))
                    return global;
            }
        }
        return null;
    }

    public IRMethod getChildMethod(String name)
    {
        for (IRNode child : children) {
            if (child instanceof IRMethod) {
                if (((IRMethod) child).getName().equals(name))
                    return ((IRMethod) child);
            }
        }
        return null;
    }
}
