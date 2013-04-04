/*
 * Copyright (c) 2007-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.*;
import java.util.List;

import hudson.plugins.graph.Identifiable;
import net.sf.json.*;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.graph.GraphTable;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Represents a plot data series configuration.
 *
 * @author Nigel Daley
 * @author Allen Reese
 */
public abstract class Series extends Identifiable
{
    protected String file;

    protected String style;

    protected Series(String id, String file, String style)
    {
	    super(id);
        this.file = file;
        this.style = style;
    }

    public String getFile()
    {
        return file;
    }

    public String getStyle()
    {
	    return style;
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

    public String getStorageName()
    {
	    return getId() + ".csv";
    }

    public File getStorageFile(AbstractProject project)
    {
        return new File(project.getConfigFile().getFile().getParentFile(), getStorageName());
    }

    public FilePath getStorageFilePath(AbstractProject project)
    {
        return new FilePath(getStorageFile(project));
    }

    protected SeriesValueStorage getStorage(AbstractProject project)
    {
	    return new SeriesValueStorage(getStorageFilePath(project));
    }

    public void updateSeriesValues(AbstractBuild build, Integer maxNumberOfBuilds) throws IOException
    {
	    SeriesValueStorage storage = getStorage(build.getProject());

	    List<SeriesValue> values = storage.read(maxNumberOfBuilds);

	    values.addAll(loadSeries(build));

	    storage.write(values);
    }

    public List<SeriesValue> loadSeriesValues(AbstractProject project) throws IOException
    {
        return getStorage(project).read();
    }

    public JSONArray getSeriesJson(AbstractProject project, JSONArray series) throws IOException
    {
	    GraphTable graphTable = new GraphTable(getStorage(project).read());

	    List<String> headers = graphTable.getHeaders();

	    for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++)
        {
            JSONObject seriesJson = new JSONObject();

            JSONArray valuesJson = new JSONArray();

            String header = headers.get(columnIndex);

            seriesJson.put("label", header);

            seriesJson.put("style", getStyle());

            for (List<String> row : graphTable.getRows())
            {
                String columnValue = row.get(columnIndex);

                valuesJson.add(isEmpty(columnValue) ? null : Double.parseDouble(columnValue));
            }

            seriesJson.put("values", valuesJson);

            series.add(seriesJson);
        }
	    return series;
    }

    /**
     * Retrieves the plot data for one series after a build from the workspace.
     *
     *
     * @param build Hudson build the data should be taken from
     * @return a list of points that should be displayed on plot
     */
    protected abstract List<SeriesValue> loadSeries(AbstractBuild build) throws IOException;
}
