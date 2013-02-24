/*
 * Copyright (c) 2007 Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the MIT License.
 */
package hudson.plugins.graph;

import hudson.model.AbstractProject;

import java.io.IOException;
import java.util.ArrayList;

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
    private AbstractProject project;

    private List<Graph> graphs;

    private String group;

    public GraphGroupView(AbstractProject project, String group, SortedSet<Graph> graphs)
    {
        this.graphs = new ArrayList<Graph>(graphs);
        this.group = group;
        this.project = project;
    }

    public String getGroup()
    {
        return group;
    }

    public List<Graph> getGraphs()
    {
        return graphs;
    }

    public AbstractProject getProject()
    {
        return project;
    }

    private JSONArray getGraphsJson() throws IOException
    {
        JSONArray graphsJson = new JSONArray();

        for (Graph graph : graphs)
        {
            graphsJson.add(graph.toJson(project));
        }

        return graphsJson;
    }

    public void doGetGroup(StaplerRequest req, StaplerResponse res) throws IOException
    {
        JSONObject groupJson = new JSONObject();

        groupJson.put("name", group);

        groupJson.put("graphs", getGraphsJson());

        res.setContentType("application/json");

        res.getWriter().write(groupJson.toString());
    }
}