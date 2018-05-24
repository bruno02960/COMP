package yal2jvm.hhir;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{
    private String name;
    private Type type;
    private Integer initVal;
    private String size;

    IRGlobal(Variable variable, Variable value) 
    {
        if (variable.getType() == Type.ARRAY)
        {
        	//if assign is size
        	//if assign is init value
        }
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
                inst1 += " I = " + (initVal != null ? initVal : 0);
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
