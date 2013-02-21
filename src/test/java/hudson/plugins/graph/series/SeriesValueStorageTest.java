package hudson.plugins.graph.series;

import hudson.FilePath;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SeriesValueStorageTest
{
    protected FilePath getStorageFile()
    {
        return new FilePath(new File("target/storage.csv"));
    }

    protected List<SeriesValue> generateSeriesValues(int numberOfValues, int numberOfValuesInBuild)
    {
        List<SeriesValue> values = new ArrayList<SeriesValue>();

        for (int index = 0, buildNumber = 0; index < numberOfValues; index++)
        {
            buildNumber += (index + 1) % numberOfValuesInBuild == 0 ? 1 : 0;

            values.add(new SeriesValue(0d, "Sample label", buildNumber));
        }

        return values;
    }

    @Test
    public void shouldBePossibleToReadFromStorageAndWriteInto() throws IOException
    {
        List<SeriesValue> seriesValues = generateSeriesValues(9, 2);

        SeriesValueStorage storage = new SeriesValueStorage(getStorageFile());

        storage.write(seriesValues);

        List<SeriesValue> loadedValues = storage.read();

        assertEquals("Unexpected number of loaded objects", seriesValues.size(), loadedValues.size());
    }

    @Test
    public void shouldBePossibleToLimitNumberOfBuildsToRead() throws IOException
    {
        List<SeriesValue> seriesValues = generateSeriesValues(9, 2);

        SeriesValueStorage storage = new SeriesValueStorage(getStorageFile());

        storage.write(seriesValues);

        assertEquals("Unexpected number of loaded objects", 6, storage.read(3).size());
    }

    @Test
    public void shouldLoadAllBuildsIfPassedArgumentIsNullOrMinusOne() throws IOException
    {
        List<SeriesValue> seriesValues = generateSeriesValues(8, 2);

        SeriesValueStorage storage = new SeriesValueStorage(getStorageFile());

        storage.write(seriesValues);

        assertEquals("Unexpected number of loaded objects", 8, storage.read(null).size());

        assertEquals("Unexpected number of loaded objects", 8, storage.read(-1).size());
    }
}