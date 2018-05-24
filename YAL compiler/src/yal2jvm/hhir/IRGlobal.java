package yal2jvm.hhir;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{
    private String name;
    private Type type;
    private Variable value = null;

    public IRGlobal(Variable variable)
    {
        this.name = variable.getVar();
        this.type = variable.getType();
    }

    public IRGlobal(Variable variable, Variable value)
    {
        this(variable);
        this.value = value;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        if(value == null)
        {
            if (type == Type.ARRAY) // a[];
            return createGlobalArray();
        else // a;
            return createGlobalInteger();
        }
        else
        {
            if (type == Type.ARRAY) // a[] = ...
            {
                if(value.getType() == Type.ARRAYSIZE) // a[] = [50];
                    return createGlobalArrayWithSize(value);
                else // a[] = 50;
                    return assignAllArrayElements(value);
            }
            else // a = ...
            {
                if(value.getType() == Type.ARRAYSIZE) // a = [50];
                    return createGlobalArrayWithSize(value);
                else // a = 50;
                    return assignVar(value);
            }
        }
    }

    private ArrayList<String> createGlobalInteger()
    {
        ArrayList<String>
        String inst = ".field public static " + name;
        inst += " I = " + (value != null ? value.getValue() : 0);

        return new ArrayList<String>().add(inst);
    }

    private ArrayList<String> assignVar(Variable value)
    {

        return null;
    }

    private ArrayList<String> assignAllArrayElements(Variable value)
    {

        return null;
    }

    private ArrayList<String> createGlobalArray()
    {

        return null;
    }

    private ArrayList<String> createGlobalArrayWithSize(Variable value)
    {

        return null;
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
