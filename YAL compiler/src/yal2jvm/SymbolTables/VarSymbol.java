package yal2jvm.SymbolTables;

public class VarSymbol extends Symbol
{
    private String type;
    private boolean initialized;
    private int size;

    public VarSymbol(String id, String type, boolean initialized)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
        this.size = -1;
    }

    public VarSymbol(String id, String type, boolean initialized, int size)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
        this.size = size;
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

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public VarSymbol getCopy()
    {
        return new VarSymbol(new String(id), new String(type), new Boolean(initialized), new Integer(size));
    }
}
