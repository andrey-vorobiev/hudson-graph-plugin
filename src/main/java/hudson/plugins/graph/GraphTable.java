package hudson.plugins.graph;

import hudson.plugins.graph.series.SeriesValue;

import java.util.*;

public class GraphTable
{
    private List<String> headers;

    private List<List<String>> rows = new ArrayList<List<String>>();

    public GraphTable(List<SeriesValue> seriesValues)
    {
        headers = getHeaders(seriesValues);

        for (Map.Entry<Integer, List<SeriesValue>> pair : groupSeriesValuesByBuildNumber(seriesValues).entrySet())
        {
            rows.add(makeRow(headers, pair.getValue()));
        }
    }

    public List<String> getHeaders()
    {
        return headers;
    }

    public List<List<String>> getRows()
    {
        return rows;
    }

    protected SortedMap<Integer, List<SeriesValue>> groupSeriesValuesByBuildNumber(List<SeriesValue> seriesValues)
    {
        SortedMap<Integer, List<SeriesValue>> groupedValues = new TreeMap<Integer, List<SeriesValue>>();

        for (SeriesValue value : seriesValues)
        {
            List<SeriesValue> chunk = groupedValues.get(value.getBuildNumber());

            if (chunk == null)
            {
                chunk = new ArrayList<SeriesValue>();

                groupedValues.put(value.getBuildNumber(), chunk);
            }

            chunk.add(value);
        }

        return groupedValues;
    }

    protected List<String> getHeaders(List<SeriesValue> seriesValues)
    {
        SortedSet<String> headers = new TreeSet<String>();

        for (SeriesValue value : seriesValues)
        {
            headers.add(value.getLabel());
        }

        return new ArrayList<String>(headers);
    }

    public SeriesValue findSeriesValueByLabel(List<SeriesValue> seriesValues, String label)
    {
        for (SeriesValue value : seriesValues)
        {
            if (label.equals(value.getLabel()))
            {
                return value;
            }
        }

        return null;
    }

    public List<String> makeRow(List<String> headers, List<SeriesValue> seriesValues)
    {
        List<String> row = new ArrayList<String>();

        for (String header : headers)
        {
            SeriesValue value = findSeriesValueByLabel(seriesValues, header);

            row.add(value == null ? "" : value.getValue().toString());
        }

        return row;
    }
}
