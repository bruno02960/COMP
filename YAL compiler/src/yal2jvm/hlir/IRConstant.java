package yal2jvm.hlir;

import java.util.ArrayList;

/**
 *
 */
public class IRConstant extends IRNode
{
    private String value;

    /**
     *
     * @param irConstant
     */
    public IRConstant(IRConstant irConstant)
    {
        super(irConstant);
        this.value = new String(irConstant.getValue());
    }

    /**
     *
     * @param value
     */
    public IRConstant(String value)
    {
        this.value = value;
        this.setNodeType("Constant");
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param value
     * @return
     */
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

    /**
     *
     * @return
     */
	public String getValue()
	{
		return value;
	}

    /**
     *
     * @param value
     */
	public void setValue(String value)
	{
		this.value = value;
	}

    /**
     *
     * @return
     */
    public Object clone()
    {
        return new IRConstant(this);
    }
}
