package yal2jvm.hhir;

import java.util.ArrayList;

public class IRStoreArith extends IRStore
{
    private IRArith irArith;

    //a = b + c
    public IRStoreArith(String name, Operation op)
    {
        this.name = name;
        this.nodeType = "StoreArith";
        irArith = new IRArith(op);
        this.addChild(irArith);
    }

    //a[i] = b + c;
    public IRStoreArith(VariableArray name, Operation op)
    {
        this.name = name.getVar();
        this.arrayAccess = true;
        this.index = new IRLoad(name.getAt());
        this.nodeType = "StoreArith";
        irArith = new IRArith(op);
        this.addChild(index);
        this.addChild(irArith);
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

        //TODO: check list of iinc use cases
        if(irArith.getLhs().nodeType.equals("Load")) {
            IRLoad arithLhs = ((IRLoad) irArith.getLhs());

            if (getRhs().nodeType.equals("Constant") && arithLhs.getName().equals(name)) {
                IRConstant irConstant = (IRConstant) getRhs();

                if (irArith.getOp().equals(Operation.ADD)) {    /* pronto para ser incrementado */
                    System.out.print("iinc ");

                    //TODO: What are the mechanics behind finding the correct register?
                    IRMethod method = (IRMethod) findParent("Method");
                    int register = method.getVarRegister(name);
                    if (register == -1)
                        register = method.getArgumentRegister(name);
                    System.out.print(register + " ");

                    System.out.println(irConstant.getValue());
                    //System.out.println("INC");
                }
                else {  /* trocar operando para negativo */
                    if(irArith.getOp().equals(Operation.SUB)) {

                    }
                }
            }
        }

        ArrayList<String> arithInst = irArith.getInstructions();
        ArrayList<String> storeInst = getInstForStoring(arrayAccess, index, irArith);

        inst.addAll(arithInst);
        inst.addAll(storeInst);
        return inst;
    }

}
