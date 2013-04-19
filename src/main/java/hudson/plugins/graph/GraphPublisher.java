/*
 * Copyright (c) 2007-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph;

import hudson.*;
import hudson.model.*;
import hudson.plugins.graph.series.Series;
import hudson.tasks.*;
import org.kohsuke.stapler.export.Exported;

import java.io.IOException;
import java.util.*;

/**
 * Records the plot data for builds.
 *
 * @author Nigel Daley
 */
public class GraphPublisher extends Recorder
{
    @Extension
    public static final GraphBuildStepDescriptor descriptor = new GraphBuildStepDescriptor();

    private List<Graph> graphs = new ArrayList<Graph>();

    @Exported
    @SuppressWarnings("unused")
    public List<Graph> getGraphs()
    {
        return graphs;
    }

    public SortedSet<Graph> getGraphs(String urlGroup)
    {
        SortedSet<Graph> matchedPlots = new TreeSet<Graph>();

        for (Graph graph : graphs)
        {
            if (urlGroup.equals(graph.getGroup()))
            {
                matchedPlots.add(graph);
            }
        }

        return matchedPlots;
    }

    public SortedSet<String> getGroups()
    {
        SortedSet<String> groups = new TreeSet<String>();

        for (Graph graph : graphs)
        {
            groups.add(graph.getGroup());
        }

        return groups;
    }

    void setGraphs(List<Graph> graphs)
    {
        this.graphs = graphs;
    }

    public List<Series> getSeries()
    {
        List<Series> series = new ArrayList<Series>();

        for (Graph graph : graphs)
        {
            series.addAll(graph.getSeries());
        }

        return series;
    }

    void deleteLegacySeries(GraphPublisher oldPublisher, Job job)
    {
        if (oldPublisher == null)
        {
            return;
        }

        List<Series> newSeries = getSeries();

        for (Series series : oldPublisher.getSeries())
        {
            if (!newSeries.contains(series))
            {
                series.delete(job);
            }
        }
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> job)
    {
        return new GraphGroupEnumerationView(job, this);
    }

    public BuildStepMonitor getRequiredMonitorService()
    {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor()
    {
        return descriptor;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException
    {
        listener.getLogger().println("Parsing series files...");

        for (Graph graph : getGraphs())
        {
            graph.handleBuild(build, listener);
        }

        return true;
    }
}