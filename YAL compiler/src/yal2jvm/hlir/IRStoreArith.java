package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRStoreArith extends IRStore
{
    private IRArith irArith;

    /**
     *
     * @param op
     */
    public IRStoreArith(Operation op)
    {
        this.setNodeType("StoreArith");
        irArith = new IRArith(op);
        this.addChild(irArith);
    }

    /**
     *
     * @param name
     * @param op
     */
    //a = b + c
    public IRStoreArith(String name, Operation op)
    {
        this(op);
        this.name = name;
    }

    /**
     *
     * @param name
     * @param op
     */
    //a[i] = b + c;
    public IRStoreArith(VariableArray name, Operation op)
    {
        this(op);
        this.name = name.getVar();
        this.arrayAccess = true;
        Variable at = name.getAt();
        if(at.getType().equals(Type.INTEGER))
            this.index = new IRConstant(at.getVar());
        else
            this.index = new IRLoad(at);
        this.addChild(index);
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
        ArrayList<String> inst = new ArrayList<>();
        boolean isIinc = checkIfIsIinc(inst);

        if(!isIinc)
        {
            ArrayList<String> storeInst = getInstForStoring(arrayAccess, index, irArith);
            inst.addAll(storeInst);
        }

        return inst;
    }

    /**
     *
     * @param inst
     * @return
     */
    private boolean checkIfIsIinc(ArrayList<String> inst)
    {
        boolean isIinc = false;

        if(irArith.getOp().equals(Operation.ADD) || irArith.getOp().equals(Operation.SUB)) {
            if (irArith.getLhs().getNodeType().equals("Load")) {
                IRLoad arithLhs = ((IRLoad) irArith.getLhs());

                if (arithLhs.getType().equals(Type.INTEGER) && getRhs().getNodeType().equals("Constant") && arithLhs.getName().equals(getName())) {
                    IRConstant irConstant = (IRConstant) getRhs();
                    if(Integer.parseInt(irConstant.getValue()) > -32768 && Integer.parseInt(irConstant.getValue()) < 32768) {
                        String instruction = getIincInstruction(irConstant);
                        if(!instruction.equals("")) {
                            inst.add(instruction);
                            isIinc = true;
                        }
                    }
                }
            } else {
                if (irArith.getRhs().getNodeType().equals("Load")) {
                    IRLoad arithRhs = ((IRLoad) irArith.getRhs());

                    if (arithRhs.getType().equals(Type.INTEGER) && getLhs().getNodeType().equals("Constant") && arithRhs.getName().equals(getName())) {
                        IRConstant irConstant = (IRConstant) getLhs();
                        if(Integer.parseInt(irConstant.getValue()) > -32768 && Integer.parseInt(irConstant.getValue()) < 32768) {
                            String instruction = getIincInstruction(irConstant);
                            if(!instruction.equals("")) {
                                inst.add(instruction);
                                isIinc = true;
                            }
                        }
                    }
                }
            }
        }

        return isIinc;
    }

    /**
     *
     * @param irConstant
     * @return
     */
    private String getIincInstruction(IRConstant irConstant) {
        String instruction = "iinc ";

        IRMethod method = (IRMethod) findParent("Method");
        int register = method.getArgumentRegister(getName());
        if (register == -1)
            register = method.getVarRegister(getName());
        if (register == -1)
            return "";

        instruction += register + " " + (irArith.getOp().equals(Operation.SUB)? "-" : "") + irConstant.getValue();

        return instruction;
    }

}
