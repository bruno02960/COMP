package yal2jvm.symbol_tables;

/**
 *
 */
public class VarSymbol extends Symbol
{

    private String type;
    private boolean initialized;

    /**
     *
     * @param id
     * @param type
     * @param initialized
     */
    public VarSymbol(String id, String type, boolean initialized)
    {
        super(id);
        this.type = type;
        this.initialized = initialized;
    }

    /**
     *
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /**
     *
     * @param initialized
     */
    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other)
    {
        return id.equals(((VarSymbol) other).getId());
    }

    /**
     *
     * @return
     */
    public VarSymbol getCopy()
    {
        return new VarSymbol(new String(id), new String(type), new Boolean(initialized));
    }

    /**
     *
     * @return
     */
    @Override
    protected Object clone()
    {
        return getCopy();
    }
}
