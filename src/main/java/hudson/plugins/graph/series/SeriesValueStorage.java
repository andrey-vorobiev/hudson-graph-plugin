package hudson.plugins.graph.series;

import com.googlecode.jcsv.annotations.internal.ValueProcessorProvider;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.AnnotationEntryParser;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import hudson.FilePath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.jcsv.CSVStrategy.UK_DEFAULT;

public class SeriesValueStorage
{
    private class SeriesValueCSVEntityConverter implements CSVEntryConverter<SeriesValue>
    {
        public String[] convertEntry(SeriesValue value)
        {
            return new String[]{value.getValue().toString(), value.getLabel(), value.getBuildNumber().toString()};
        }
    }

    private FilePath storage;

    public SeriesValueStorage(FilePath storage)
    {
        this.storage = storage;

        try
        {
            if (!storage.exists())
            {
                storage.touch(System.currentTimeMillis());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected CSVReader<SeriesValue> createReader() throws IOException
    {
        CSVReaderBuilder<SeriesValue> builder = new CSVReaderBuilder<SeriesValue>(new InputStreamReader(storage.read()));

        builder.strategy(UK_DEFAULT);
        builder.entryParser(new AnnotationEntryParser<SeriesValue>(SeriesValue.class, new ValueProcessorProvider()));

        return builder.build();
    }

    public List<SeriesValue> read() throws IOException
    {
        CSVReader<SeriesValue> reader = createReader();

        return reader.readAll();
    }

    protected int findMaxBuildNumber(List<SeriesValue> values)
    {
        return values.isEmpty() ? -1 : values.get(values.size() - 1).getBuildNumber();
    }

    protected boolean shouldIncludeAll(Integer maxNumberOfBuilds)
    {
        return maxNumberOfBuilds == null || maxNumberOfBuilds == -1;
    }

    public List<SeriesValue> read(Integer maxNumberOfBuilds) throws IOException
    {
        List<SeriesValue> values = read(), matchedValues = new ArrayList<SeriesValue>();

        int maxBuildNumber = findMaxBuildNumber(values);

        for (SeriesValue value : values)
        {
            if (shouldIncludeAll(maxNumberOfBuilds) || value.getBuildNumber() > maxBuildNumber - maxNumberOfBuilds)
            {
                matchedValues.add(value);
            }
        }

        return matchedValues;
    }

    protected CSVWriter<SeriesValue> createWriter(FileWriter writer)
    {
        CSVWriterBuilder<SeriesValue> builder = new CSVWriterBuilder<SeriesValue>(writer);

        builder.strategy(UK_DEFAULT);
        builder.entryConverter(new SeriesValueCSVEntityConverter());

        return builder.build();
    }

    public void write(List<SeriesValue> values) throws IOException
    {
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(storage.getRemote());

            CSVWriter<SeriesValue> csvWriter = createWriter(writer);

            csvWriter.writeAll(values);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
}
