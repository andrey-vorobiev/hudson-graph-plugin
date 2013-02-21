/*
 * Copyright (c) 2007-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.IOException;
import java.util.List;

import hudson.model.AbstractBuild;

/**
 * Represents a plot data series configuration.
 *
 * @author Nigel Daley
 * @author Allen Reese
 */
public abstract class Series
{
    protected String file;

    protected Series(String file)
    {
        this.file = file;
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
