package hudson.plugins.graph.helper;

import net.sf.json.JSONArray;
import org.junit.Test;

import static hudson.plugins.graph.helper.JSonHelper.makeJsonArray;
import static org.junit.Assert.*;

public class JSonHelperTest
{
    @Test
    public void makeJsonArray_shouldReturnEmptyArrayIfNullIsPassed()
    {
        JSONArray array = makeJsonArray(null);

        assertEquals("Unexpected array size", 0, array.size());
    }
}
