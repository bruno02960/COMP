package yal2jvm.hhir;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{
    private String name;
    private Type type;
    private IRNode rhs;
    boolean arraySize = false;

    IRGlobal(Variable variable, Variable value) {
        switch (variable.getType()) {
            case ARRAY:
                name = variable.getVar();
                type = Type.ARRAY;
                if (value.getType().equals(Type.INTEGER))
                    this.rhs = new IRConstant(value.getVar());
                else
                    this.rhs = new IRLoad(value);
                break;
            case VARIABLE:
                name = variable.getVar();
                type = Type.VARIABLE;
                if (value.getType().equals(Type.INTEGER))
                    this.rhs = new IRConstant(value.getVar());
                else
                    this.rhs = new IRLoad(value);
                break;
        }

        if (variable.getType() == Type.ARRAY)
        {
        	//if assign is size
        	//if assign is init value
        }
    }

    IRGlobal(Variable variable, Variable value, Type valueType) {

        assert valueType == Type.ARRAYSIZE;

        name = variable.getVar();
        type = Type.ARRAY;
        arraySize = true;
        if(value.getType().equals(Type.INTEGER))
            this.rhs = new IRConstant(value.getVar());
        else
            this.rhs = new IRLoad(value);
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        String inst1 = ".field public static " + name;
        switch (type)
        {
            case INTEGER:
            {
//                inst1 += " I = " + (rhs.initVal != null ? initVal : 0);
                break;
            }
            case ARRAY:
            {
            	break;
            }
            default:
                break;
        }

        inst.add(inst1);
        return inst;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return this.type;
    }
    
    public ArrayList<String>getInitializationInstructions()
    {
		ArrayList<String> inst = new ArrayList<>();
		
		//if size
		
		//else if init 
		
		return inst;
    }
}
