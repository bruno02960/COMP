package yal2jvm.hlir;

public class Variable
{
    private String var = null;
    private boolean sizeAccess = false;
    private Type type;

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

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isSizeAccess()
    {
        return sizeAccess;
    }

    public Type getType() {
        return type;
    }

    public String getVar() {
        return var;
    }
}