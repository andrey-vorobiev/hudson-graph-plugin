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

import hudson.plugins.graph.MockBuild;
import java.io.File;
import java.io.IOException;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Node;

import static org.mockito.Mockito.*;

public class SeriesTest
{
    protected FilePath sampleWorkspace = new FilePath(new File("target/test-classes"));
    
    public AbstractBuild mockBuild() throws IOException
    {
        Node node = mock(Node.class);
        when(node.createPath(sampleWorkspace.getRemote())).thenReturn(sampleWorkspace);
        
        MockBuild build = mock(MockBuild.class, CALLS_REAL_METHODS);
        build.setWorkspace(sampleWorkspace);
        when(build.getBuiltOn()).thenReturn(node);

        return build;
    }
}
