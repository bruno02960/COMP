package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRReturn extends IRNode
{
    private String name;
    private Type type;

    public IRReturn(String name, Type type)
    {
        this.name = name;
        this.type = type;
        this.nodeType = "Return";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        switch (type)
        {
            case VOID:
                inst.add("return");
                break;

            case INTEGER:
                IRMethod irMethod = (IRMethod) getParent();
                int registerNumber = irMethod.getVarRegister(name);
                String loadInst = getInstructionToLoadRegisterToStack(registerNumber);
                inst.add(loadInst);
                break;

            case ARRAY:
                System.out.println("Not generating HHIR for return value ARRAY yet.");
        }

        return inst;
    }

}
