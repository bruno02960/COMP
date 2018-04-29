package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRLoad extends IRNode
{

    private String name;
    private int register = -1;

    public IRLoad(String name)
    {
        this.name = name;
        this.nodeType = "Load";
    }

    public int getRegister()
    {
        return register;
    }

    public void setRegister(int register)
    {
        this.register = register;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        IRMethod method = null;
        IRNode par = this.parent;
        while (true)
        {
            if (par.toString().equals("Method"))
            {
                method = (IRMethod) par;
                break;
            } else
                par = par.getParent();
        }

        int register = method.getVarRegister(name);
        if (register == -1)
            register = method.getArgumentRegister(name);

        if (register > -1)	//variable is local
        {
            inst.add("iload " + register);
        } else				//variable is global
        {
            IRModule module = ((IRModule) method.getParent());
            IRGlobal global = module.getGlobal(name);
            if (global == null)
            	return null;

            String in = "getstatic " + module.getName() + "/" + global.getName() + " ";
            in += global.getType() == Type.INTEGER ? "I" : "A";
            inst.add(in);
        }

        return inst;
    }

}
