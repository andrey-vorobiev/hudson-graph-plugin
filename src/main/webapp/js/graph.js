/*
 * The MIT License
 *
 * Copyright 2013 Hudson.
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

var hgp = hgp || {};

hgp.delegate = function(object, handler)
{
    handler = handler || this;

    return function()
    {
        handler.apply(object, arguments);
    };
};

hgp.Graph = function(json)
{
    this.json = json;

    this.numberOfValues = json.series[0] ? json.series[0].values.length : 0;
};

hgp.Graph.prototype.getSeriesValues = function(series)
{
    var values = [];

    for (var valueIndex = 0; valueIndex < series.values.length; valueIndex++)
    {
        values.push([valueIndex, series.values[valueIndex]]);
    }

    return values;
};

hgp.Graph.prototype.getSeriesType = function(series)
{
    switch (this.json.style)
    {
        case 'area':
            return {
                lines: {fill: true}
            };
        case 'bar':
            return {
                bars: {show: true, grouped: true, fillOpacity: 1}
            };
        default:
            return {
                lines: {show: true}
            };
    }
};

hgp.Graph.prototype.getSeries = function()
{
    var series = [], jsonSeries = this.json.series;

    for (var seriesIndex = 0; seriesIndex < jsonSeries.length; seriesIndex++)
    {
        var json = {
            label: jsonSeries[seriesIndex].label,
            data: this.getSeriesValues(jsonSeries[seriesIndex])
        };

        series.push(Flotr._.extend(json, this.getSeriesType(jsonSeries[seriesIndex])));
    }

    return series;
};

hgp.Graph.prototype.getTicks = function()
{
    var ticks = [], xLabels = this.json.xLabels;

    for (var labelIndex = 0; labelIndex < xLabels.length; labelIndex++)
    {
        ticks.push([labelIndex, xLabels[labelIndex]]);
    }

    return ticks;
};

hgp.Graph.prototype.getOptions = function()
{
    return {
        title: this.json.name,
        HtmlText: false,
        legend: {
            position: 'ne'
        },
        spreadsheet: {
            show : true
        },
        yaxis: {
            title: this.json.yLabel,
            scaling: this.json.logscaling ? 'logarithmic' : 'linear'
        },
        xaxis: {
            title: 'Build numbers',
            labelsAngle: 90,
            ticks: this.getTicks()
        },
        selection: {
            mode: 'xy'
        }
    };
};

hgp.Graph.prototype.drawGraph = function(container, options)
{
    var mergedOptions = Flotr._.extend(this.getOptions(), options || {});

    Flotr.draw(container, this.getSeries(), mergedOptions);
}

hgp.Graph.prototype.embedGraph = function(container)
{
    this.drawGraph(container, {});

    Flotr.EventAdapter.observe(container, 'flotr:select', hgp.delegate(this, function(area){
        this.drawGraph(container, {
            xaxis: {
                min:area.x1,
                max:area.x2
                },
            yaxis: {
                min:area.y1,
                max:area.y2
                }
        });
    }));

    Flotr.EventAdapter.observe(container, 'flotr:click', hgp.delegate(this, function(){
        this.drawGraph(container, {});
    }));

    container.style.backgroundImage = 'none';
};