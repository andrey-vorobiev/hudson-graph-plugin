/*
 * Copyright (c) 2007-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.*;
import java.util.List;

import hudson.model.Job;
import hudson.plugins.graph.Identifiable;
import net.sf.json.*;

import hudson.FilePath;
import hudson.model.AbstractBuild;
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

    protected Series(String id, String file)
    {
        super(id);
        this.file = file;
    }

    public String getFile()
    {
        return file;
    }

    @SuppressWarnings("unused")
    public abstract String getType();

    public String getStorageFileName()
    {
        return getId() + ".csv";
    }

    public File getStorageFile(Job job)
    {
        return new File(job.getConfigFile().getFile().getParentFile(), getStorageFileName());
    }

    public FilePath getStorageFilePath(Job job)
    {
        return new FilePath(getStorageFile(job));
    }

    protected SeriesValueStorage getStorage(Job job)
    {
        return new SeriesValueStorage(getStorageFilePath(job));
    }

    public void updateSeriesValues(AbstractBuild build, Integer maxNumberOfBuilds) throws IOException
    {
        SeriesValueStorage storage = getStorage(build.getProject());

        List<SeriesValue> values = storage.read(maxNumberOfBuilds);

        values.addAll(loadSeries(build));

        storage.write(values);
    }

    public List<SeriesValue> loadSeriesValues(Job job) throws IOException
    {
        return getStorage(job).read();
    }

    public void delete(Job job)
    {
        getStorageFile(job).delete();
    }

    public JSONArray getSeriesJson(Job job, JSONArray series) throws IOException
    {
        GraphTable graphTable = new GraphTable(getStorage(job).read());

        List<String> headers = graphTable.getHeaders();

        for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++)
        {
            JSONObject seriesJson = new JSONObject();

            JSONArray valuesJson = new JSONArray();

            String header = headers.get(columnIndex);

            seriesJson.put("label", header);

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
