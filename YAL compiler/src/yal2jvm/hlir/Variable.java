package yal2jvm.hlir;

/**
 *
 */
public class Variable
{
    private String var = null;
    private boolean sizeAccess = false;
    private Type type;

    /**
     *
     * @param var
     * @param type
     */
    Variable(String var, Type type)
    {
        this.type = type;

        if(var != null) /* var can be null in case of CALL type */
        {
            if(var.contains(".size"))
            {
                sizeAccess = true;
                this.var = var.split(".size")[0];
            }
            else {
                this.var = var;
            }
        }
    }

    /**
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public boolean isSizeAccess()
    {
        return sizeAccess;
    }

    /**
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public String getVar() {
        return var;
    }
}
