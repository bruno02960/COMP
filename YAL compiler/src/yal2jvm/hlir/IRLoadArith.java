package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRLoadArith extends IRNode
{
    private IRArith irArith;

    /**
     *
     * @param op
     */
    public IRLoadArith(Operation op)
    {
        this.setNodeType("LoadArith");
        irArith = new IRArith(op);
        this.addChild(irArith);
    }

    /**
     *
     * @return
     */
    public IRNode getRhs()
    {
        return irArith.getRhs();
    }

    /**
     *
     * @param rhs
     */
    public void setRhs(IRNode rhs)
    {
        this.irArith.setRhs(rhs);
    }

    /**
     *
     * @return
     */
    public IRNode getLhs()
    {
        return irArith.getLhs();
    }

    /**
     *
     * @param lhs
     */
    public void setLhs(IRNode lhs)
    {
        this.irArith.setLhs(lhs);
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<String> getInstructions()
    {
        return irArith.getInstructions();
    }

}
