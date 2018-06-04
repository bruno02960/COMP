package yal2jvm.hlir;

import yal2jvm.utils.Utils;

import java.util.ArrayList;

/**
 *
 */
public class IRArith extends IRNode {
    private Operation op;
    private IRNode rhs, lhs;

    /**
     *
     * @param op
     */
    public IRArith(Operation op)
    {
        this.op = op;
        this.setNodeType("Arith");
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<String> lhsInst = lhs.getInstructions();
        ArrayList<String> rhsInst = rhs.getInstructions();

        String opInst = null;

        switch (op)
        {
            case ADD:
                opInst = "iadd";
                break;
            case SUB:
                opInst = "isub";
                break;
            case MULT:
                opInst = "imul";
                break;
            case DIV:
                opInst = "idiv";
                break;
            case SHIFT_R:
                opInst = "ishr";
                break;
            case SHIFT_L:
                opInst = "ishl";
                break;
            case USHIFT_R:
                opInst = "iushl";
                break;
            case AND:
                opInst = "iand";
                break;
            case OR:
                opInst = "ior";
                break;
            case XOR:
                opInst = "lxor";
                break;
        }


        inst.addAll(lhsInst);
        inst.addAll(rhsInst);
        inst.add(opInst);
        return inst;
    }

    /**
     *
     * @return
     */
    public IRNode getRhs()
    {
        return rhs;
    }

    /**
     *
     * @param rhs
     */
    public void setRhs(IRNode rhs)
    {
        this.rhs = rhs;
        this.rhs.setParent(this);
    }

    /**
     *
     * @return
     */
    public IRNode getLhs()
    {
        return lhs;
    }

    /**
     *
     * @param lhs
     */
    public void setLhs(IRNode lhs)
    {
        this.lhs = lhs;
        this.lhs.setParent(this);
    }

    /**
     *
     * @return
     */
    public Operation getOp() {
        return op;
    }

    /**
     *
     * @return
     */
    public String getStringValueIfBothConstant()
    {
        IRMethod method = (IRMethod) findParent("Method");

        String lhsValue = getValueIfConstant(method, lhs);
        if(lhsValue == null)
            return null;


        //TODO
        // else if(((IRLoad)lhs).getLoadedConstantValue() != null)
        //            lhsValue = ((IRLoad)lhs).getLoadedConstantValue();

        String rhsValue = getValueIfConstant(method, rhs);
        if(rhsValue == null)
            return null;


        //TODO
        //  else if(((IRLoad)rhs).getLoadedConstantValue() != null)
        //            rhsValue = ((IRLoad)rhs).getLoadedConstantValue();

        return String.valueOf(Utils.getOperationValue(lhsValue, rhsValue, op.getSymbol()));
    }

    private String getValueIfConstant(IRMethod method, IRNode node)
    {
        String lhsValue;
        if(node instanceof IRConstant)
            lhsValue = ((IRConstant)node).getValue();
        else
        {
            IRLoad load = (IRLoad)node;
            String varName = getVarNameForConstantName(load.getName(), load.getIndex());
            lhsValue = method.getConstValueByConstVarName(varName).getValue();
        }

        return lhsValue;
    }
}
