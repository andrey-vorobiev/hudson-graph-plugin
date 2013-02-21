package hudson.plugins.graph;

import hudson.FilePath;
import hudson.model.*;
import hudson.plugins.graph.series.*;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Graph implements Comparable<Graph>
{
    private String id;

    private String group;

    private String name;

    private String style;

    private String yLabel;

    private Boolean logScaling;

    private Integer numberOfBuildsToUse;

    private List<Series> series;

    @DataBoundConstructor
    public Graph(String group, String name, String style, String yLabel, Boolean logScaling, Integer numberOfBuildsToUse, String id)
    {
        this.name = name;
        this.group = group;
        this.style = style;
        this.yLabel = yLabel;
        this.logScaling = logScaling;
        this.numberOfBuildsToUse = numberOfBuildsToUse;
        this.id = isEmpty(id) ? randomUUID().toString() : id;
    }

    public String getId()
    {
        return id;
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    public String getStyle()
    {
        return style;
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

    public void setSeries(List<Series> series)
    {
        this.series = series;
    }

    public Integer getNumberOfBuildsToUse()
    {
        return numberOfBuildsToUse;
    }

    public String getStorageName()
    {
        return id + ".csv";
    }

    protected SeriesValueStorage getStorage(AbstractProject project)
    {
        File storageFile = new File(project.getConfigFile().getFile().getParentFile(), getStorageName());

        return new SeriesValueStorage(new FilePath(storageFile));
    }

    public void addBuild(AbstractBuild build, BuildListener listener) throws IOException
    {
        SeriesValueStorage storage = getStorage(build.getProject());

        List<SeriesValue> values = storage.read(numberOfBuildsToUse);

        for (Series currentSeries : this.series)
        {
            try
            {
                List<SeriesValue> loadedValues = currentSeries.loadSeries(build);

                values.addAll(loadedValues);
            }
            catch (IOException e)
            {
                listener.getLogger().println("Series load failed: " + currentSeries + ", " + e.getLocalizedMessage());
            }
        }

        storage.write(values);
    }

    public List<SeriesValue> getSeriesValues(AbstractProject project) throws IOException
    {
        return getStorage(project).read(getNumberOfBuildsToUse());
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

    public JSONArray getSeriesJson(GraphTable graphTable)
    {
        JSONArray array = new JSONArray();

        List<String> headers = graphTable.getHeaders();

        for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++)
        {
            JSONObject seriesJson = new JSONObject();

            JSONArray values = new JSONArray();

            String header = headers.get(columnIndex);

            seriesJson.put("label", header);

            for (List<String> row : graphTable.getRows())
            {
                String columnValue = row.get(columnIndex);

                values.add(isEmpty(columnValue) ? null : Double.parseDouble(columnValue));
            }

            seriesJson.put("values", values);

            array.add(seriesJson);
        }

        return array;
    }

    public int compareTo(Graph o)
    {
        return name.compareTo(o.name);
    }

    public JSONObject toJson(AbstractProject project) throws IOException
    {
        List<SeriesValue> values = getSeriesValues(project);

        JSONObject graphJson = new JSONObject();

        graphJson.put("name", getName());
        graphJson.put("style", getStyle());
        graphJson.put("yLabel", getYLabel());
        graphJson.put("xLabels", getXLabelsJson(values));
        graphJson.put("logscaling", getLogScaling());
        graphJson.put("series", getSeriesJson(new GraphTable(values)));

        return graphJson;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Graph{");

        sb.append("id=").append(id).append(", ");
        sb.append("group=").append(group).append(", ");
        sb.append("name=").append(name).append(", ");
        sb.append("yLabel=").append(yLabel).append(", ");
        sb.append("logScaling=").append(logScaling).append(", ");
        sb.append("numberOfBuildsToUse=").append(numberOfBuildsToUse).append(", ");
        sb.append("series=").append(series);

        return sb.append('}').toString();
    }
}