package yal2jvm.hlir;


import java.util.ArrayList;

public class IRArgument extends IRNode
{
    private int register;

    public IRArgument(int register)
    {
        this.register = register;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        return null;
    }

    public int getRegister()
    {
        return register;
    }
}
