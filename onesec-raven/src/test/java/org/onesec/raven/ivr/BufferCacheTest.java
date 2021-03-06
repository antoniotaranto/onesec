/*
 *  Copyright 2011 Mikhail Titov.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.onesec.raven.ivr;

import javax.media.Buffer;
import org.junit.Test;
import org.onesec.raven.OnesecRavenTestCase;
import static org.easymock.EasyMock.*;
import org.easymock.IArgumentMatcher;
import org.raven.log.LogLevel;
import org.raven.sched.ExecutorService;
import org.raven.sched.Task;
import org.raven.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Mikhail Titov
 */
public class BufferCacheTest extends OnesecRavenTestCase
{
    private Logger logger = LoggerFactory.getLogger(BufferCacheTest.class);
    
    @Test
    public void serviceTest() throws Exception
    {
        ExecutorService executor = createMock(ExecutorService.class);
        Node node = createMock(Node.class);
        expect(node.isLogLevelEnabled(anyObject(LogLevel.class))).andReturn(Boolean.TRUE).anyTimes();
        expect(node.getLogger()).andReturn(logger).anyTimes();
        executor.execute(executeTask());
        expectLastCall().anyTimes();
        replay(executor, node);
        
        BufferCache cache = registry.getService(BufferCache.class);
        assertNotNull(cache);
        Buffer silentBuffer = cache.getSilentBuffer(executor, node, Codec.G711_A_LAW, 160);
        assertNotNull(silentBuffer);
        
        verify(executor, node);
    }
    
    public static Task executeTask() {
        reportMatcher(new IArgumentMatcher() {
            public boolean matches(Object argument) {
                final Task task = (Task) argument;
                new Thread(new Runnable() {
                    public void run() {
                        task.run();
                    }
                }).start();
                return true;
            }
            public void appendTo(StringBuffer buffer) { }
        });
        return null;
    }
}