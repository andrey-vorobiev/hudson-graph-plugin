package hudson.plugins.graph;

import hudson.model.*;
import hudson.plugins.graph.series.*;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.export.Exported;

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

    @Exported
    @SuppressWarnings("unused")
    public String getGroup()
    {
        return group;
    }

    @Exported
    @SuppressWarnings("unused")
    public String getName()
    {
        return name;
    }

    @Exported
    @SuppressWarnings("unused")
    public String getYLabel()
    {
        return yLabel;
    }

    @Exported
    @SuppressWarnings("unused")
    public Boolean getLogScaling()
    {
        return logScaling;
    }

    @Exported
    @SuppressWarnings("unused")
    public Integer getNumberOfBuildsToUse()
    {
        return numberOfBuildsToUse;
    }

    public List<Series> getSeries()
    {
        return series;
    }

    public void addSeries(Series series)
    {
        getSeries().add(series);
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

    public List<SeriesValue> getSeriesValues(Job job) throws IOException
    {
        List<SeriesValue> seriesValues = new ArrayList<SeriesValue>();

        for (Series currentSeries : series)
        {
            seriesValues.addAll(currentSeries.loadSeriesValues(job));
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

    public JSONArray getSeriesJson(Job job) throws IOException
    {
        JSONArray array = new JSONArray();

        for (Series currentSeries : series)
        {
            currentSeries.getSeriesJson(job, array);
        }

        return array;
    }

    public JSONObject toJson(Job job) throws IOException
    {
        List<SeriesValue> values = getSeriesValues(job);

        JSONObject graphJson = new JSONObject();

        graphJson.put("name", getName());
        graphJson.put("yLabel", getYLabel());
        graphJson.put("xLabels", getXLabelsJson(values));
        graphJson.put("logscaling", getLogScaling());
        graphJson.put("series", getSeriesJson(job));

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
