package yal2jvm.symbol_tables;

public class VarSymbol extends Symbol
{

    private String type;
    private boolean initialized;

    public VarSymbol(String id, String type, boolean initialized)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        return id.equals(((VarSymbol) other).getId());
    }

    public VarSymbol getCopy()
    {
        return new VarSymbol(new String(id), new String(type), new Boolean(initialized));
    }

    @Override
    protected Object clone()
    {
        return getCopy();
    }
}
