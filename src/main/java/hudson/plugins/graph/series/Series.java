/*
 * Copyright (c) 2007-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hudson.model.AbstractBuild;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Represents a plot data series configuration.
 *
 * @author Nigel Daley
 * @author Allen Reese
 */
public abstract class Series
{
    public enum Type
    {
        CSV("csv"), PROPERTIES("properties"), XML("xml");

        private String value;

        private Type(String value)
        {
            this.value = value;
        }

        public static Type parse(String value)
        {
            for (Type seriesType : values())
            {
                if (seriesType.value.equals(value))
                {
                    return seriesType;
                }
            }
            throw new IllegalArgumentException("Unexpected series type: " + value);
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    protected String file;

    /**
     *
     * @param file
     */
    protected Series(String file)
    {
        this.file = file;
    }

    /**
     * Creates series from JSON data using StaplerRequest
     *
     * @param json series in JSON representation
     * @param req stapler request
     * @return series
     */
    public static Series createSeries(JSONObject json, StaplerRequest req)
    {
        JSONObject seriesJson = json.getJSONObject("seriesType");

        seriesJson.put("file", json.getString("file"));

        String seriesType = seriesJson.getString("value");

        switch (Type.parse(seriesType))
        {
            case CSV:
                return req.bindJSON(CSVSeries.class, seriesJson);
            case PROPERTIES:
                return req.bindJSON(PropertiesSeries.class, seriesJson);
            case XML:
                return req.bindJSON(XMLSeries.class, seriesJson);
            default:
                throw new IllegalArgumentException("Unexpected series type:" + seriesType);
        }
    }

    /**
     * Creates list of series from JSON data using StaplerRequest
     *
     * @param array JSON array of series
     * @param req stapler request
     * @return series list
     */
    public static List<Series> createSeries(JSONArray array, StaplerRequest req)
    {
        List<Series> series = new ArrayList<Series>();

        for (Object seriesJson : array)
        {
            series.add(createSeries((JSONObject) seriesJson, req));
        }

        return series;
    }

    public String getFile()
    {
        return file;
    }

    protected String nvl(String... values)
    {
        for (String value : values)
        {
            if (value != null)
            {
                return value;
            }
        }

        return null;
    }

    public abstract String getType();

    /**
     * Retrieves the plot data for one series after a build from the workspace.
     *
     *
     * @param build Hudson build the data should be taken from
     * @return a list of points that should be displayed on plot
     */
    public abstract List<SeriesValue> loadSeries(AbstractBuild build) throws IOException;
}
