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
package hudson.plugins.graph;

import hudson.plugins.graph.series.Series;

import java.util.List;
import java.util.ArrayList;
import net.sf.json.*;
import org.kohsuke.stapler.StaplerRequest;

import static hudson.plugins.graph.series.SeriesFactory.newSeries;
import static hudson.plugins.graph.helper.JsonHelper.makeJsonArray;

public class GraphFactory
{
    public static Graph newGraph(JSONObject graphJson, StaplerRequest req)
    {
        Graph graph = req.bindJSON(Graph.class, graphJson);

        for (Series series: newSeries(makeJsonArray(graphJson.get("series")), req))
        {
            graph.addSeries(series);
        }

        return graph;
    }

    public static List<Graph> newGraphs(JSON graphsJson, StaplerRequest req)
    {
        List<Graph> graphs = new ArrayList<Graph>();

        for (Object graphJson : makeJsonArray(graphsJson))
        {
            graphs.add(newGraph((JSONObject)graphJson, req));
        }

        return graphs;
    }
}
