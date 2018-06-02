package yal2jvm.hlir;

import java.util.ArrayList;

public class IRStoreArith extends IRStore
{
    private IRArith irArith;

    public IRStoreArith(Operation op)
    {
        this.setNodeType("StoreArith");
        irArith = new IRArith(op);
        this.addChild(irArith);
    }

    //a = b + c
    public IRStoreArith(String name, Operation op)
    {
        this(op);
        this.name = name;
    }

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
        ArrayList<String> inst = new ArrayList<>();
        boolean isIinc = checkIfIsIinc(inst);

        if(!isIinc)
        {
            ArrayList<String> arithInst = irArith.getInstructions();
            ArrayList<String> storeInst = getInstForStoring(arrayAccess, index, irArith);

            inst.addAll(arithInst);
            inst.addAll(storeInst);
        }

        return inst;
    }

    private boolean checkIfIsIinc(ArrayList<String> inst)
    {
        boolean isIinc = false;

        if(irArith.getOp().equals(Operation.ADD) || irArith.getOp().equals(Operation.SUB)) {
            if (irArith.getLhs().getNodeType().equals("Load")) {
                IRLoad arithLhs = ((IRLoad) irArith.getLhs());

                if (arithLhs.getType().equals(Type.INTEGER) && getRhs().getNodeType().equals("Constant") && arithLhs.getName().equals(name)) {
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

                    if (arithRhs.getType().equals(Type.INTEGER) && getLhs().getNodeType().equals("Constant") && arithRhs.getName().equals(name)) {
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

    private String getIincInstruction(IRConstant irConstant) {
        String instruction = "iinc ";

        IRMethod method = (IRMethod) findParent("Method");
        int register = method.getArgumentRegister(name);
        if (register == -1)
            register = method.getVarRegister(name);
        if (register == -1)
            return "";

        instruction += register + " " + (irArith.getOp().equals(Operation.SUB)? "-" : "") + irConstant.getValue();

        return instruction;
    }

}
