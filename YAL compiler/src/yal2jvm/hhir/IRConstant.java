package yal2jvm.hhir;

import java.util.ArrayList;

public class IRConstant extends IRNode
{
    private String value;

    public IRConstant(String value)
    {
        this.value = value;
        this.nodeType = "Constant";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        try
        {
            int integer = Integer.parseInt(value);
            inst.add(getLoadConstantInstruction(integer));
        }
        catch(NumberFormatException nfe)
        {
            inst.add("ldc " + value);
        }

        return inst;
    }

    public static String getLoadConstantInstruction(int value)
    {
        if(value < 6)
            return "iconst_" + value;
        else
            return "ldc " + value;
    }

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
