package yal2jvm.SymbolTables;


public class Symbol
{
    protected String id;

    public Symbol(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object other)
    {
        return this.id.equals(((Symbol)other).getId());
    }
}
