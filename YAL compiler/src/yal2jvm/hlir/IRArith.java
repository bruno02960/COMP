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
        String opInst = getOpInstructions();

        if(HLIR.optimize)
        {
            String constantenessValue = getOperandsInstructionsIfConstantness();
            if(constantenessValue != null)
            {
                IRConstant constant = new IRConstant(constantenessValue);
                inst.addAll(constant.getInstructions());
                return inst;
            }
        }


        inst.addAll(lhsInst);
        inst.addAll(rhsInst);
        inst.add(opInst);
        return inst;
    }

    private String getOperandsInstructionsIfConstantness()
    {
        String lhsValue = checkOperandForConstantness(lhs);
        if(lhsValue == null)
            return null;

        String rhsValue = checkOperandForConstantness(rhs);
        if(rhsValue == null)
            return null;

        Integer operationValue = Utils.getOperationValueByOperator(lhsValue, rhsValue, op);

        return operationValue.toString();
    }

    private String checkOperandForConstantness(IRNode node)
    {
        if(node instanceof IRConstant)
            return ((IRConstant)node).getValue();
        else if(node instanceof IRLoad)
            return ((IRLoad)node).getLoadedConstantValue();
        else
            return null;
    }

    /**
     *
     * @return the jvm code as string of the attribute operation
     */
    private String getOpInstructions()
    {
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
        return opInst;
    }

    /**
     * Returns this object's rhs field
     * @return  rhs field value
     */
    public IRNode getRhs()
    {
        return rhs;
    }

    /**
     * Sets this object's rhs field to the rhs parameter
     * @param rhs   the new value of this object's rhs field
     */
    public void setRhs(IRNode rhs)
    {
        this.rhs = rhs;
        this.rhs.setParent(this);
    }

    /**
     * Returns this object's lhs field
     * @return lhs field value
     */
    public IRNode getLhs()
    {
        return lhs;
    }

    /**
     * Sets this object's lhs field to the rhs parameter
     * @param lhs    the new value of this object's lhs field
     */
    public void setLhs(IRNode lhs)
    {
        this.lhs = lhs;
        this.lhs.setParent(this);
    }

    /**
     * Returns this object's op field
     * @return  op field value
     */
    public Operation getOp() {
        return op;
    }

    /**
     * Confirms if the lhs and rhs values are constants or not, and calls the function that processes
     * the operation between the two values and turns the result of that function into a String
     * @return  the value of the operation between the lhs and rhs if they are both constants.
     *          null if one or both of them are not constants or the operator between them isn't recognized
     *          in the function getOperationValue
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

    /**
     *
     * @param method
     * @param node
     * @return
     */
    private String getValueIfConstant(IRMethod method, IRNode node)
    {
        if(node instanceof IRConstant)
            return ((IRConstant)node).getValue();
        else if(node instanceof IRLoad)
        {
            IRLoad load = (IRLoad)node;
            String varName = getVarNameForConstantName(load.getName(), load.getIndex());
            IRConstant constant = method.getConstValueByConstVarName(varName);
            if(constant != null)
                return constant.getValue();
        }

        return null;
    }
}
