package yal2jvm.hlir;

import java.util.ArrayList;

public class IRConstant extends IRNode
{
    private String value;

    public IRConstant(IRConstant irConstant)
    {
        super(irConstant);
        this.value = new String(irConstant.getValue());
    }

    public IRConstant(String value)
    {
        this.value = value;
        this.setNodeType("Constant");
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
            inst.add("ldc " + value);                       /* if value is string type */
        }

        return inst;
    }

    public static String getLoadConstantInstruction(int value)
    {
        if(value <= 5 && value >= -1) {
            if(value == -1) {
                return "iconst_m1";
            }
            else {
                return "iconst_" + value;
            }
        }
        else {
            if (value <= 32767 && value >= -32768) {
                if(value <= 127 && value >= -128 ) {
                    return "bipush " + value;
                }
                else {
                    return "sipush " + value;
                }
            }
            else {
                return "ldc " + value;
            }
        }
    }

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

    public Object clone()
    {
        return new IRConstant(this);
    }
}
