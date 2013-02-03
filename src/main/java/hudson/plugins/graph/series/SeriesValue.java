/*
 * Copyright (c) 2008-2009 Yahoo! Inc.  All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.plugins.graph.series;

import com.googlecode.jcsv.annotations.MapToColumn;
import hudson.model.AbstractBuild;
import hudson.model.Build;

import static java.lang.Double.parseDouble;

public class SeriesValue
{
    @MapToColumn(column = 0)
    private Double value;

    @MapToColumn(column = 1)
    private String url;

    @MapToColumn(column = 2)
    private String label;

    @MapToColumn(column = 3)
    private Integer buildNumber;

    public SeriesValue()
    {
        //serialization support
    }

    public SeriesValue(Double value, String url, String label, Integer buildNumber)
    {
        this.url = url;
        this.value = value;
        this.label = label;
        this.buildNumber = buildNumber;
    }
    
    public SeriesValue(String value, String url, String label, AbstractBuild build)
    {
        this(parseDouble(value), url, label, build.getNumber());
    }

    public Double getValue()
    {
        return value;
    }

    public String getUrl()
    {
        return url;
    }

    public String getLabel()
    {
        return label;
    }

    public Integer getBuildNumber()
    {
        return buildNumber;
    }

    @Override
    public String toString()
    {
        return "SeriesValue{label=" + label + ", value=" + value + ", url=" + url + ", buildNumber=" + buildNumber + "}";
    }
}
