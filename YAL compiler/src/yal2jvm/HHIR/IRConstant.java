package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRConstant extends IRNode
{

    String value;
    Type type;

    public IRConstant(String value, Type type)
    {
        this.value = value;
        this.type = type;
        this.nodeType = "Constant";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String inst1 = "ldc ";
        inst1 += this.type == Type.STRING ? "\"" + value + "\"" : value;

        inst.add(inst1);
        return inst;
    }
}
