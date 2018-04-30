package yal2jvm.hhir;

public class PairStringType
{

    private Type type;
    private String string;

    public PairStringType(String string, Type type)
    {
        this.type = type;
        this.string = string;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }
}
