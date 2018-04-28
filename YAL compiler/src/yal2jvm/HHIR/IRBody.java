package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRBody extends IRNode
{

    public IRBody()
    {
        this.nodeType = "Body";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        return inst;
    }

}
