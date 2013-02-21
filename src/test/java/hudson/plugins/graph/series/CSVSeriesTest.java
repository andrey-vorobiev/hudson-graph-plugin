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
import static hudson.plugins.graph.series.CSVSeries.FilteringMode.*;

public class CSVSeriesTest extends SeriesTest
{
    protected String sampleFile = "sample.csv";

    @Test
    public void shouldNotLoadAnySeriesIfFileNotExists() throws IOException
    {
        Series series = new CSVSeries("noop", OFF.name(), null);

        assertEquals("Unexpected number of points", 0, series.loadSeries(mockBuild()).size());
    }

    @Test
    public void shouldLoadSeriesFromCsvFile() throws IOException
    {
        Series series = new CSVSeries(sampleFile, OFF.name(), null);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of points", 5, points.size());
    }

    @Test
    public void shouldExcludeColumnsByName() throws IOException
    {
        String columnNamesToExclude = "a,c";

        Series series = new CSVSeries(sampleFile, EXCLUDE_BY_COLUMN.name(), columnNamesToExclude);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of points", 3, points.size());

        for (SeriesValue point : points)
        {
            assertTrue("Point should excluded: " + point, !columnNamesToExclude.contains(point.getLabel()));
        }
    }

    @Test
    public void shouldExcludeColumnsByIndexes() throws IOException
    {
        String columnIndexesToExclude = "1,0,3";

        Series series = new CSVSeries(sampleFile, EXCLUDE_BY_INDEX.name(), columnIndexesToExclude);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of points", 2, points.size());

        for (SeriesValue point : points)
        {
            Integer value = point.getValue().intValue();

            assertTrue("Point should excluded: " + point, !columnIndexesToExclude.contains(value.toString()));
        }
    }

    @Test
    public void shouldIncludeColumnsByName() throws IOException
    {
        String columnNamesToInclude = "a,c";

        Series series = new CSVSeries(sampleFile, INCLUDE_BY_COLUMN.name(), columnNamesToInclude);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of points", 2, points.size());

        for (SeriesValue point : points)
        {
            assertTrue("Point should be included: " + point, columnNamesToInclude.contains(point.getLabel()));
        }
    }

    @Test
    public void shouldIncludeColumnsByIndexes() throws IOException
    {
        String columnIndexesToInclude = "1,0,3";

        Series series = new CSVSeries(sampleFile, INCLUDE_BY_INDEX.name(), columnIndexesToInclude);

        List<SeriesValue> points = series.loadSeries(mockBuild());

        assertEquals("Unexpected number of points", 3, points.size());

        for (SeriesValue point : points)
        {
            Integer value = point.getValue().intValue();

            assertTrue("Point should be included: " + point, columnIndexesToInclude.contains(value.toString()));
        }
    }
}
