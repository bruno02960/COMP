package yal2jvm.SymbolTables;

public class VarSymbol extends Symbol
{

    private String type;
    private boolean initialized;
    private boolean sizeSet;

    public VarSymbol(String id, String type, boolean initialized, boolean sizeSet)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
        this.sizeSet = sizeSet;
    }

    public VarSymbol(String id, String type, boolean initialized)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
        this.sizeSet = false;
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

    public boolean isSizeSet()
    {
        return sizeSet;
    }

    public void setSizeSet(boolean sizeSet)
    {
        this.sizeSet = sizeSet;
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
        return new VarSymbol(new String(id), new String(type), new Boolean(initialized), new Boolean(sizeSet));
    }

    @Override
    protected Object clone()
    {
        return getCopy();
    }
}
