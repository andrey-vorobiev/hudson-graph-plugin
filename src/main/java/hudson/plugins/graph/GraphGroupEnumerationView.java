/*
 * Copyright (c) 2007 Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the MIT License.
 */
package hudson.plugins.graph;

import hudson.model.Action;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import hudson.model.Job;
import org.kohsuke.stapler.*;
import org.kohsuke.stapler.export.Exported;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;

/**
 * UI model that displays list of available graph groups enumeration. Almost of the public methods are called by
 * jelly from GraphGroupEnumerationView/*.jelly
 *
 * @author Nigel Daley
 */
public class GraphGroupEnumerationView implements Action, StaplerProxy
{
    private GraphPublisher publisher;

    private Job job;

    public GraphGroupEnumerationView(Job job, GraphPublisher publisher)
    {
        this.job = job;
        this.publisher = publisher;
    }

    @Exported
    @SuppressWarnings("unused")
    public Job getJob()
    {
        return job;
    }

    public String getDisplayName()
    {
        return Messages.Plot_Action_DisplayName();
    }

    public String getIconFileName()
    {
        return "graph.gif";
    }

    public String getUrlName()
    {
        return Messages.Plot_UrlName();
    }

    public boolean hasGraphs() throws IOException
    {
        return !publisher.getGraphs().isEmpty();
    }

    @Exported
    @SuppressWarnings("unused")
    public Set<String> getGroups()
    {
        return publisher.getGroups();
    }

    @Exported
    @SuppressWarnings("unused")
    public String encodeGroup(String group) throws IOException
    {
        return encode(group, "UTF-8");
    }

    @Exported
    @SuppressWarnings("unused")
    public GraphGroupView getDynamic(String urlEncodedGroup, StaplerRequest req, StaplerResponse rsp) throws IOException
    {
        String originalGroup = decode(urlEncodedGroup, "UTF-8");

        return new GraphGroupView(job, originalGroup, publisher.getGraphs(originalGroup));
    }

    public Object getTarget()
    {
        SortedSet<String> groups = publisher.getGroups();

        if (groups.size() == 1)
        {
            String singleGroup = groups.first();

            return new GraphGroupView(job, singleGroup, publisher.getGraphs(singleGroup));
        }
        else
        {
            return this;
        }
    }
}
