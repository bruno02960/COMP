package yal2jvm.hhir;

import java.util.ArrayList;

public class IRArith extends IRNode {
    private Operation op;
    private IRNode rhs, lhs;

    IRArith(Operation op)
    {
        this.op = op;
        this.nodeType = "Arith";
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
}
