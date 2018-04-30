package yal2jvm.symbol_tables;

import java.util.Objects;

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
        return id.equals(((Symbol) other).getId());
    }

    public Symbol getCopy()
    {
        return new Symbol(new String(id));
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
