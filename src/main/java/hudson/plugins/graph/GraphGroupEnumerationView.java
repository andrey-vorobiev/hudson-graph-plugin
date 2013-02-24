/*
 * Copyright (c) 2007 Yahoo! Inc.  All rights reserved.
 * Copyrights licensed under the MIT License.
 */
package hudson.plugins.graph;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Project;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import org.kohsuke.stapler.*;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;

/**
 * UI model that displays list of available graph groups enumeration. Almost of the public methods are called by
 * jelly from GraphGroupEnumerationView/*.jelly
 *
 * @author Nigel Daley
 */
@SuppressWarnings("unused")
public class GraphGroupEnumerationView implements Action, StaplerProxy
{
    private AbstractProject project;

    private GraphPublisher publisher;

    public GraphGroupEnumerationView(AbstractProject project, GraphPublisher publisher)
    {
        this.project = project;
        this.publisher = publisher;
    }

    public AbstractProject getProject()
    {
        return project;
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

    public Set<String> getGroups()
    {
        return publisher.getGroups();
    }

    public String encodeGroup(String group) throws IOException
    {
        return encode(group, "UTF-8");
    }

    public GraphGroupView getDynamic(String urlEncodedGroup, StaplerRequest req, StaplerResponse rsp) throws IOException
    {
        String originalGroup = decode(urlEncodedGroup, "UTF-8");

        return new GraphGroupView(project, originalGroup, publisher.getGraphs(originalGroup));
    }

    // If there's only one graph group, simply display that group contents instead this view.
    public Object getTarget()
    {
        SortedSet<String> groups = publisher.getGroups();

        if (groups.size() == 1)
        {
            String singleGroup = groups.first();

            return new GraphGroupView(project, singleGroup, publisher.getGraphs(singleGroup));
        }
        else
        {
            return this;
        }
    }
}
