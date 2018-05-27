package yal2jvm.hhir;


import java.util.ArrayList;

public class IRArgument extends IRNode
{
    private int register;

    IRArgument(int register)
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
