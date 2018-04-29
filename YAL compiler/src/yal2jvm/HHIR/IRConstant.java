package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRConstant extends IRNode
{
    private String value;

    IRConstant(String value)
    {
        this.value = value;
        this.nodeType = "Constant";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String inst1 = "ldc ";
        inst1 += value;

        inst.add(inst1);
        return inst;
    }
}
