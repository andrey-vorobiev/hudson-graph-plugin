/*
 * The MIT License
 *
 * Copyright 2012 Hudson.
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

import hudson.FilePath;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.*;

import static hudson.plugins.graph.GraphFactory.newGraphs;
import net.sf.json.JSON;

public class GraphBuildStepDescriptor extends BuildStepDescriptor<Publisher>
{
    public GraphBuildStepDescriptor()
    {
        super(GraphPublisher.class);
    }

    public String getDisplayName()
    {
        return Messages.Plot_Publisher_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType)
    {
        return Project.class.isAssignableFrom(jobType);
    }

    @Override
    public Publisher newInstance(StaplerRequest req, JSONObject publisherJson) throws Descriptor.FormException
    {
        GraphPublisher publisher = new GraphPublisher();

        for (Graph graph : newGraphs((JSON) publisherJson.get("graphs"), req))
        {
            publisher.addGraph(graph);
        }

        return publisher;
    }

    public FormValidation doCheckSeriesFile(@AncestorInPath AbstractProject project, @QueryParameter String value) throws IOException
    {
        return FilePath.validateFileMask(project.getSomeWorkspace(), value);
    }
}
