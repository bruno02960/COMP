package yal2jvm.hlir;

import yal2jvm.utils.Utils;

import java.util.ArrayList;

public class IRArith extends IRNode {
    private Operation op;
    private IRNode rhs, lhs;

    public IRArith(Operation op)
    {
        this.op = op;
        this.setNodeType("Arith");
    }

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

    public IRNode getRhs()
    {
        return rhs;
    }

    public void setRhs(IRNode rhs)
    {
        this.rhs = rhs;
        this.rhs.setParent(this);
    }

    public IRNode getLhs()
    {
        return lhs;
    }

    public void setLhs(IRNode lhs)
    {
        this.lhs = lhs;
        this.lhs.setParent(this);
    }

    public Operation getOp() {
        return op;
    }

    public String getStringValueIfBothConstant()
    {
        IRMethod method = (IRMethod) findParent("Method");
        String lhsValue;
        if(lhs instanceof IRConstant)
            lhsValue = ((IRConstant)lhs).getValue();
        else if(method.getConstValueByConstVarName(((IRLoad)lhs).getName()) != null)
            lhsValue = method.getConstValueByConstVarName(((IRLoad)lhs).getName()).getValue();
        else
            return null;

        //TODO
        // else if(((IRLoad)lhs).getLoadedConstantValue() != null)
        //            lhsValue = ((IRLoad)lhs).getLoadedConstantValue();

        String rhsValue;
        if(rhs instanceof IRConstant)
            rhsValue = ((IRConstant)rhs).getValue();
        else if(method.getConstValueByConstVarName(((IRLoad)rhs).getName()) != null)
            rhsValue = method.getConstValueByConstVarName(((IRLoad)rhs).getName()).getValue();
        else
            return null;

        //TODO
        //  else if(((IRLoad)rhs).getLoadedConstantValue() != null)
        //            rhsValue = ((IRLoad)rhs).getLoadedConstantValue();

        return String.valueOf(Utils.getOperationValue(lhsValue, rhsValue, op.getSymbol()));
    }
}
