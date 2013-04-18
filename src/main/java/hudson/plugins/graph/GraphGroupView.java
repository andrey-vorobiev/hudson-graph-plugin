/*
 * Copyright (c) 2007 Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the MIT License.
 */
package hudson.plugins.graph;

import java.io.IOException;
import java.util.ArrayList;

import hudson.model.Job;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.List;
import java.util.SortedSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * UI model that displays graphs list. Almost of the public methods are called by jelly from GraphGroupView/*.jelly
 *
 * @author Nigel Daley
 */
@SuppressWarnings("unused")
public class GraphGroupView
{
    private List<Graph> graphs;

    private Job job;

    private String group;

    public GraphGroupView(Job job, String group, SortedSet<Graph> graphs)
    {
        this.job = job;
        this.group = group;
        this.graphs = new ArrayList<Graph>(graphs);
    }

    public Job getJob()
    {
        return job;
    }

    public String getGroup()
    {
        return group;
    }

    public List<Graph> getGraphs()
    {
        return graphs;
    }

    private JSONArray getGraphsJson(StaplerRequest req) throws IOException
    {
        JSONArray graphsJson = new JSONArray();

        for (Graph graph : graphs)
        {
            graphsJson.add(graph.toJson(req.findAncestorObject(Job.class)));
        }

        return graphsJson;
    }

    public void doGetGroup(StaplerRequest req, StaplerResponse res) throws IOException
    {
        JSONObject groupJson = new JSONObject();

        groupJson.put("name", group);

        groupJson.put("graphs", getGraphsJson(req));

        res.setContentType("application/json");

        res.getWriter().write(groupJson.toString());
    }
}