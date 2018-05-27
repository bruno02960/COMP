package yal2jvm.hhir;

import java.util.ArrayList;

public class IRLoadArith extends IRNode
{
    private IRArith irArith;

    IRLoadArith(Operation op)
    {
        this.nodeType = "LoadArith";
        irArith = new IRArith(op);
        this.addChild(irArith);
    }

    public IRNode getLhs()
    {
        return irArith.getLhs();
    }

    public void setLhs(IRNode lhs)
    {
        this.irArith.setLhs(lhs);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        return irArith.getInstructions();
    }

}
