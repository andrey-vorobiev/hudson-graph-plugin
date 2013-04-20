/*
 * The MIT License
 *
 * Copyright 2013 Hudson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.graph.series;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class SeriesFactory
{
    /**
     * Creates series from JSON data using StaplerRequest
     *
     * @param seriesJson series in JSON representation
     * @param req stapler request
     * @return series
     */
    public static Series parseSeries(JSONObject seriesJson, StaplerRequest req)
    {
        JSONObject seriesTypeJson = seriesJson.getJSONObject("seriesType");

        seriesTypeJson.put("id", seriesJson.getString("id"));
        seriesTypeJson.put("file", seriesJson.getString("file"));

        switch (SeriesType.parse(seriesTypeJson.getString("value")))
        {
            case CSV:
                return req.bindJSON(CSVSeries.class, seriesTypeJson);
            case PROPERTIES:
                return req.bindJSON(PropertiesSeries.class, seriesTypeJson);
            case XML:
                return req.bindJSON(XMLSeries.class, seriesTypeJson);
            default:
                throw new IllegalArgumentException("Unexpected series type:" + seriesTypeJson);
        }
    }

    /**
     * Creates list of series from JSON data using StaplerRequest
     *
     * @param array JSON array of series
     * @param req stapler request
     * @return series list
     */
    public static List<Series> parseSeries(JSONArray array, StaplerRequest req)
    {
        List<Series> series = new ArrayList<Series>();

        for (Object seriesJson : array)
        {
            series.add(parseSeries((JSONObject) seriesJson, req));
        }

        return series;
    }
}
