package yal2jvm.hhir;

public class Variable
{
    private String var = null;
    private boolean sizeAccess = false;
    private Integer value = null;
    private Type type;

    Variable(String var, Type type)
    {
        this.var = var;
        this.type = type;

        //TODO: Verify if it is possible to use type to distinguish between variables and constants
        if(var != null) /* var can be null in case of CALL type */
        {
            try
            {
                value = Integer.parseInt(this.var);
            }
            catch (NumberFormatException ignored)
            {
                //case var is not a constant, is a variable
                this.var = null;
            }

            assert this.var != null;
            if(this.var.contains(".size"))
            {
                sizeAccess = true;
                this.var = this.var.split(".size")[0];
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
