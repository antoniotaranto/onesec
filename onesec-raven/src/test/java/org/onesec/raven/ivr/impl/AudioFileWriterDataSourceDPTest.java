/*
 * Copyright 2016 Mikhail Titov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onesec.raven.ivr.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PushBufferDataSource;
import org.junit.Before;
import org.junit.Test;
import org.onesec.raven.OnesecRavenModule;
import org.onesec.raven.OnesecRavenTestCase;
import org.onesec.raven.ivr.CodecManager;
import org.onesec.raven.ivr.InputStreamSource;
import org.raven.dp.RavenFuture;
import org.raven.log.LogLevel;
import org.raven.sched.ExecutorService;
import org.raven.test.TestDataProcessorFacade;
import org.raven.test.TestDataProcessorFacadeConfig;
import org.raven.tree.impl.LoggerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.onesec.raven.ivr.impl.AudioFileWriterDataSourceDP.*;
import org.raven.dp.DataProcessorFacade;

/**
 *
 * @author Mikhail Titov
 */
public class AudioFileWriterDataSourceDPTest extends OnesecRavenTestCase {
    private final static Logger logger = LoggerFactory.getLogger(ContainerParserDataSource.class);
    private final static LoggerHelper loggerHelper = new LoggerHelper(LogLevel.TRACE, "Logger", null, logger);
    
    private CodecManager codecManager;
    private ExecutorService executor;
    
    @Override
    protected void configureRegistry(Set<Class> builder) {
        super.configureRegistry(builder); 
        OnesecRavenModule.ENABLE_LOADING_SOUND_RESOURCE = false;
        OnesecRavenModule.ENABLE_LOADING_TEMPLATES = false;
    }
    
    @Before
    public void prepare() throws Exception {
        codecManager = new CodecManagerImpl(logger);
        executor = createExecutor();
    }
    
    @Test
    public void testOneChannel() throws Exception {
        TestDataProcessorFacade writer = createAudioWriter();
        PushBufferDataSource ds = createDataSourceFromFile("src/test/wav/test2.wav");
        File out = new File("target/audio_writer_testOneChannel.wav");
        ask(writer, new Init(out, new PushBufferDataSource[]{ds}, codecManager, FileTypeDescriptor.WAVE), INITIALIZED_MESSAGE, 100);
        ask(writer, START_MESSAGE, STARTED_MESSAGE, 100);
        waitForStop(writer, 15);
//        RavenFuture res = writer.ask();
//        assertEquals(INITIALIZED_MESSAGE, res.get(100, TimeUnit.MILLISECONDS));
        
    }
    
    @Test
    public void testTwoChannel() throws Exception {
        TestDataProcessorFacade writer = createAudioWriter();
        PushBufferDataSource ds1 = createDataSourceFromFile("src/test/wav/test.wav");
        PushBufferDataSource ds2 = createDataSourceFromFile("src/test/wav/test2.wav");
        File out = new File("target/audio_writer_testTwoChannel.wav");
        ask(writer, new Init(out, new PushBufferDataSource[]{ds1, ds2}, codecManager, FileTypeDescriptor.WAVE), INITIALIZED_MESSAGE, 100);
        ask(writer, START_MESSAGE, STARTED_MESSAGE, 100);
        waitForStop(writer, 15);
//        assertEquals(true, writer.watch().get(15, TimeUnit.SECONDS));
//        RavenFuture res = writer.ask();
//        assertEquals(INITIALIZED_MESSAGE, res.get(100, TimeUnit.MILLISECONDS));
        
    }
    
    private void ask(DataProcessorFacade facade, Object req, Object resp, long timeout) throws Exception {
        RavenFuture res = facade.ask(req);
        assertEquals(resp, res.get(timeout, TimeUnit.MILLISECONDS));        
    }
    
    private void waitForStop(DataProcessorFacade facade, long timeout) throws Exception {
        assertEquals(true, facade.watch().get(timeout, TimeUnit.SECONDS));        
    }
    
    protected TestDataProcessorFacade createAudioWriter() {
        TestDataProcessorFacade writer = new TestDataProcessorFacadeConfig(
                "AudioWriter", testsNode, new AudioFileWriterDataSourceDP(), executor, loggerHelper
        ).buildTestFacade();
        return writer;
    }
    
    private PushBufferDataSource createDataSourceFromFile(String filename) throws FileNotFoundException {
        InputStreamSource source = new TestInputStreamSource(filename);
        IssDataSource dataSource = new IssDataSource(source, FileTypeDescriptor.WAVE);
        ContainerParserDataSource parser = new ContainerParserDataSource(codecManager, dataSource);
        PullToPushConverterDataSource conv = new PullToPushConverterDataSource(parser, executor, testsNode);
        return conv;
    }
    
}
