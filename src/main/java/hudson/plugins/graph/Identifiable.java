package hudson.plugins.graph;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public abstract class Identifiable
{
    private String id;

    public Identifiable(String id)
    {
        this.id = isEmpty(id) ? randomUUID().toString() : id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        return o != null && o instanceof Identifiable && id.equals(((Identifiable) o).id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
