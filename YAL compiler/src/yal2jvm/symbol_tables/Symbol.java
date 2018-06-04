package yal2jvm.symbol_tables;

import java.util.Objects;

/**
 *
 */
public class Symbol
{

    protected String id;

    /**
     *
     * @param id
     */
    public Symbol(String id)
    {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other)
    {
        return id.equals(((Symbol) other).getId());
    }

    /**
     *
     * @return
     */
    public Symbol getCopy()
    {
        return new Symbol(new String(id));
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
