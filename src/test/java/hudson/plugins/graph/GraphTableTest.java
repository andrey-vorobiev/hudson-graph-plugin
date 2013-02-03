package hudson.plugins.graph;

import hudson.plugins.graph.series.SeriesValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphTableTest
{
    @Test
    public void tableShouldBeCorrectlyGenerated()
    {
        List<SeriesValue> seriesValues = new ArrayList<SeriesValue>();
        
        seriesValues.add(new SeriesValue(0.0, null, "a", 1));
        seriesValues.add(new SeriesValue(1.0, null, "b", 1));
        
        seriesValues.add(new SeriesValue(1.0, null, "a", 2));
        seriesValues.add(new SeriesValue(2.0, null, "b", 2));
        seriesValues.add(new SeriesValue(3.0, null, "c", 2));
        
        GraphTable graphTable = new GraphTable(seriesValues);
        
        assertEquals("Unexpected headers", Arrays.asList("a", "b", "c"), graphTable.getHeaders());
        
        assertEquals("Unexpected number of rows", 2, graphTable.getRows().size());
    }
}
