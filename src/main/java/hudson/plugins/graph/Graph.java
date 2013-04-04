package hudson.plugins.graph;

import hudson.FilePath;
import hudson.model.*;
import hudson.plugins.graph.series.*;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Graph extends Identifiable implements Comparable<Graph>
{
    private String group;

    private String name;

    private String yLabel;

    private Boolean logScaling;

    private Integer numberOfBuildsToUse;

    private List<Series> series = new ArrayList<Series>();

    @DataBoundConstructor
    public Graph(String id, String group, String name, String yLabel, Boolean logScaling, Integer numberOfBuildsToUse)
    {
        super(id);
        this.name = name;
        this.group = group;
        this.yLabel = yLabel;
        this.logScaling = logScaling;
        this.numberOfBuildsToUse = numberOfBuildsToUse;
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    public String getYLabel()
    {
        return yLabel;
    }

    public Boolean getLogScaling()
    {
        return logScaling;
    }

    public List<Series> getSeries()
    {
        return series;
    }

    public void addSeries(Series series)
    {
        getSeries().add(series);
    }

    public Integer getNumberOfBuildsToUse()
    {
        return numberOfBuildsToUse;
    }

    public void handleBuild(AbstractBuild build, BuildListener listener)
    {
	    for (Series currentSeries : series)
	    {
	        try
	        {
		        currentSeries.updateSeriesValues(build, getNumberOfBuildsToUse());
	        }
	        catch (IOException e)
	        {
		        listener.getLogger().println("Series load failed: " + currentSeries + ", " + e.getLocalizedMessage());
	        }
	    }
    }

    public List<SeriesValue> getSeriesValues(AbstractProject project) throws IOException
    {
        List<SeriesValue> seriesValues = new ArrayList<SeriesValue>();

        for (Series currentSeries : series)
        {
            seriesValues.addAll(currentSeries.loadSeriesValues(project));
        }

        return seriesValues;
    }

    public List<String> getXLabels(List<SeriesValue> values)
    {
        LinkedHashSet<String> xLabels = new LinkedHashSet<String>();

        for (SeriesValue value : values)
        {
            xLabels.add("#" + value.getBuildNumber());
        }

        return new ArrayList<String>(xLabels);
    }

    public JSONArray getXLabelsJson(List<SeriesValue> values)
    {
        JSONArray array = new JSONArray();

        for (String xLabel: getXLabels(values))
        {
            array.add(xLabel);
        }

        return array;
    }

    public JSONArray getSeriesJson(AbstractProject project) throws IOException
    {
        JSONArray array = new JSONArray();

        for (Series currentSeries : series)
        {
            currentSeries.getSeriesJson(project, array);
        }

        return array;
    }

    public JSONObject toJson(AbstractProject project) throws IOException
    {
        List<SeriesValue> values = getSeriesValues(project);

        JSONObject graphJson = new JSONObject();

        graphJson.put("name", getName());
        graphJson.put("yLabel", getYLabel());
        graphJson.put("xLabels", getXLabelsJson(values));
        graphJson.put("logscaling", getLogScaling());
        graphJson.put("series", getSeriesJson(project));

        return graphJson;
    }

    public int compareTo(Graph o)
    {
        return name.compareTo(o.name);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Graph{");

        sb.append("group=").append(group).append(", ");
        sb.append("name=").append(name).append(", ");
        sb.append("yLabel=").append(yLabel).append(", ");
        sb.append("logScaling=").append(logScaling).append(", ");
        sb.append("numberOfBuildsToUse=").append(numberOfBuildsToUse).append(", ");
        sb.append("series=").append(series);

        return sb.append('}').toString();
    }
}
