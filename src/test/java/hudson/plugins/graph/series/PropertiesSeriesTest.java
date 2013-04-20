/*
 * The MIT License
 *
 * Copyright 2012 Hudson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.graph.series;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertiesSeriesTest extends SeriesTest
{
    protected String sampleFile = "sample.properties";

    protected String samleLabel = "label";

    @Test
    public void shouldNotLoadAnySeriesIfFileNotExists() throws IOException
    {
        Series series = new PropertiesSeries(sampleId, "noop", samleLabel);

        assertEquals("Unexpected number of points", 0, series.loadSeries(mockBuild()).size());
    }

    @Test
    public void shouldLoadSeriesFromPropertiesFile() throws IOException
    {
        Series series = new PropertiesSeries(sampleId, sampleFile, samleLabel);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of values", 1, points.size());

        SeriesValue point = points.iterator().next();

        assertEquals("Unexpected point value", 1.0, point.getValue(), 0);
        assertEquals("Unexpected point label", samleLabel, point.getLabel());
    }

    @Test
    public void labelShouldBeNullIfNotSpecifiedInSeriesConfiguration() throws IOException
    {
        Series series = new PropertiesSeries(sampleId, sampleFile, null);

        SeriesValue point = series.loadSeries(mockBuild()).iterator().next();

        assertNull("Unexpected point label", point.getLabel());
    }
}
