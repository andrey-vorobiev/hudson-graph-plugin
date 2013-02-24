/*
 * Copyright (c) 2008-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.reader.internal.DefaultCSVEntryParser;
import hudson.FilePath;

import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a plot data series configuration from an CSV file.
 *
 * @author Allen Reese
 *
 */
public class CSVSeries extends Series
{
    public static enum FilteringMode
    {
        OFF,
        INCLUDE_BY_COLUMN,
        EXCLUDE_BY_COLUMN,
        INCLUDE_BY_INDEX,
        EXCLUDE_BY_INDEX
    }

    private FilteringMode mode = FilteringMode.OFF;

    private String columns;

    /**
     *
     * @param file csv file to take data from
     * @param mode flag that describes how specified columns should be
     * considered, i.e is it column indexes or named or should be columns included
     * or excluded.
     * @param columns comma separated list of columns or indexes to exclude or
     * include.
     */
    @DataBoundConstructor
    public CSVSeries(String id, String file, String style, String mode, String columns)
    {
        super(id, file, style);

        this.columns = columns;
        this.mode = FilteringMode.valueOf(mode);
    }

    public String getType()
    {
        return SeriesType.CSV.toString();
    }

    public String getMode()
    {
        return mode.name();
    }

    protected boolean shouldIncludeColumn(String header, String columnIndex)
    {
        switch (mode)
        {
            case INCLUDE_BY_COLUMN:
                return columns.contains(header);
            case INCLUDE_BY_INDEX:
                return columns.contains(columnIndex);
            case EXCLUDE_BY_COLUMN:
                return !columns.contains(header);
            case EXCLUDE_BY_INDEX:
                return !columns.contains(columnIndex);
            default:
                return true;
        }
    }

    @Override
    protected List<SeriesValue> loadSeries(AbstractBuild build) throws IOException
    {
        List<SeriesValue> values = new ArrayList<SeriesValue>();

        FilePath seriesFile = new FilePath(build.getWorkspace(), getFile());

        try
        {
            if (!seriesFile.exists())
            {
                return values;
            }

            CSVReader<String[]> reader = null;

            try
            {
                reader = new CSVReaderBuilder<String[]>(new InputStreamReader(seriesFile.read())).strategy(CSVStrategy.UK_DEFAULT).entryParser(new DefaultCSVEntryParser()).build();

                List<String> headers = reader.readHeader();

                for (String[] row; (row = reader.readNext()) != null;)
                {
                    if (row.length == 0 || row[0].isEmpty())
                    {
                        continue;
                    }

                    for (Integer columnIndex = 0; columnIndex < row.length; columnIndex++)
                    {
                        String header = headers.get(columnIndex);

                        if (shouldIncludeColumn(header, columnIndex.toString()))
                        {
                            values.add(new SeriesValue(row[columnIndex], header, build));
                        }
                    }

                    break;
                }
            }
            finally
            {
                if (reader != null)
                {
                    reader.close();
                }
            }

            return values;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("CSVSeries{");

        sb.append("file=").append(file).append(", ");
        sb.append("mode=").append(mode).append(", ");
        sb.append("columns=").append(columns);

        return sb.append("}").toString();
    }
}
