package yal2jvm.hhir;

import java.util.ArrayList;

public class IRLoadArith extends IRLoad
{
    private IRArith irArith;
    private boolean indexSizeAccess;

    public IRLoadArith(String name, Operation op, boolean arraySizeAccess)
    {
        super(name, arraySizeAccess);
        this.nodeType = "LoadArith";
        irArith = new IRArith(op);
    }

    public IRLoadArith(String name, Operation op, String index, boolean indexSizeAccess) //TODO for arrays
    {
        super(name);
        this.nodeType = "LoadArith";
        irArith = new IRArith(op);
        this.indexSizeAccess = indexSizeAccess;
    }

    public IRNode getRhs()
    {
        return irArith.getRhs();
    }

    public void setRhs(IRNode rhs)
    {
        this.irArith.setRhs(rhs);
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
