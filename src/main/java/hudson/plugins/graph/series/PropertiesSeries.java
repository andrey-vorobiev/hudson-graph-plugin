/*
 * Copyright (c) 2008-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import java.io.*;
import java.util.*;

import hudson.FilePath;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Allen Reese
 *
 */
public class PropertiesSeries extends Series
{
    private String label;

    /**
     *
     * @param file properties file to take data from
     * @param label
     */
    @DataBoundConstructor
    public PropertiesSeries(String file, String label)
    {
        super(file);

        this.label = label;
    }

    public String getType()
    {
        return SeriesType.PROPERTIES.toString();
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public List<SeriesValue> loadSeries(AbstractBuild build) throws IOException
    {
        List<SeriesValue> values = new ArrayList<SeriesValue>();

        FilePath seriesFile = new FilePath(build.getWorkspace(), getFile());

        try
        {
            if (!seriesFile.exists())
            {
                return values;
            }

            InputStream inputStream = null;

            try
            {
                inputStream = seriesFile.read();

                Properties properties = new Properties();

                properties.load(inputStream);

                String value = properties.getProperty("YVALUE");

                String valueLabel = nvl(properties.getProperty("LABEL"), label);

                if (!isEmpty(value))
                {
                    values.add(new SeriesValue(value, valueLabel, build));
                }
            }
            finally
            {
                if (inputStream != null)
                {
                    inputStream.close();
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
        return "PropertiesSeries{file=" + file + ", label=" + label + "}";
    }
}
