package yal2jvm.hhir;

public class Variable
{
    private String var = null;
    private boolean sizeAccess = false;
    private Integer value = null;
    private Type type;

    Variable(String var, Type type)
    {
        this.type = type;

        //TODO: Verify if it is possible to use type to distinguish between variables and constants
        if(var != null) /* var can be null in case of CALL type */
        {
            try
            {
                value = Integer.parseInt(var);
            }
            catch (NumberFormatException e)
            {
                //case var is not a constant, is a variable
                this.var = var;
            }

            if(var.contains(".size"))
            {
                sizeAccess = true;
                this.var = var.split(".size")[0];
            }
        }
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isSizeAccess()
    {
        return sizeAccess;
    }

    public Integer getValue()
    {
        return value;
    }

    public Type getType() {
        return type;
    }

    public String getVar() {
        return var;
    }
}
