package yal2jvm.hhir;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{
    private boolean arraySize = false;
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

    public IRGlobal(Variable variable, Variable value, Type arraySize)
    {
        this(variable, value);
        assert arraySize == Type.ARRAYSIZE;
        this.arraySize = true;
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
        ArrayList<String> insts = new ArrayList<>();
        String inst = ".field public static " + name;
        inst += " I = " + (value != null ? value.getVar() : 0);
        insts.add(inst);

        return insts;
    }

    private ArrayList<String> createGlobalArray()
    {
        ArrayList<String> insts = new ArrayList<>();
        String inst = ".field public static " + name;
        inst += " I = " + (value != null ? value.getVar() : 0);
        insts.add(inst);

        return insts;
    }

    private ArrayList<String> assignVar(Variable value)
    {
        ArrayList<String> insts = new ArrayList<>();


        return insts;
    }

    private ArrayList<String> assignAllArrayElements(Variable value)
    {
        ArrayList<String> insts = new ArrayList<>();


        return insts;
    }

    private ArrayList<String> createGlobalArrayWithSize(Variable value)
    {
        ArrayList<String> insts = new ArrayList<>();


        return insts;
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

