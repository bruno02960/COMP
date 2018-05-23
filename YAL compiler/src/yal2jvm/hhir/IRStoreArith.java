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
    }

    //a[i] = b + c;
    public IRStoreArith(VariableArray name, Operation op) //TODO for arrays
    {
        this.name = name.getVar();
        this.nodeType = "StoreCall";
        irArith = new IRArith(op);
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

        ArrayList<String> arithInst = irArith.getInstructions();
        ArrayList<String> storeInst = getInstForStoring();

        inst.addAll(arithInst);
        inst.addAll(storeInst);
        return inst;
    }

}
