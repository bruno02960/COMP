package yal2jvm.hhir;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{

    private String name;
    private Type type;
    private Integer initVal;
    private String size;

    /**
     * Used for global variables of type integer
     *
     * @param name	integer name
     * @param type	integer type
     * @param initVal	integer initial value
     */
    public IRGlobal(String name, Type type, Integer initVal, boolean arraySizeAccess)
    {
        this.name = name;
        assert type == Type.INTEGER;
        this.type = type;
        this.initVal = initVal;
        this.nodeType = "Global";
    }

    /**
     * Used for global variables of type array
     *
     * @param name	array name
     * @param type	array type
     * @param initVal	array initial value
     * @param size	array size
     */
    public IRGlobal(String name, Type type, Integer initVal, String size, boolean arraySizeAccess)
    {
        this.name = name;
        assert type == Type.ARRAY;
        this.type = type;
        this.initVal = initVal;
        this.nodeType = "Global";
        this.size = size;
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
                break;
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
}
