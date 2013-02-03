package hudson.plugins.graph;

import hudson.FilePath;
import hudson.model.*;

import java.io.IOException;

public abstract class MockBuild<P extends AbstractProject<P, R>, R extends AbstractBuild<P, R>> extends AbstractBuild<P, R>
{
    protected MockBuild(P job) throws IOException
    {
        super(job);
    }

    @Override
    public void setWorkspace(FilePath workspace)
    {
        super.setWorkspace(workspace);
    }
}
