/*
 *  Copyright 2009 Mikhail Titov.
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

package org.onesec.raven.ivr.impl;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.media.Controller;
import javax.media.DataSink;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.SourceCloneable;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import org.onesec.raven.ivr.IncomingRtpStream;
import org.onesec.raven.ivr.IncomingRtpStreamDataSourceListener;
import org.onesec.raven.ivr.RTPManagerService;
import org.onesec.raven.ivr.RtpStreamException;
import org.raven.log.LogLevel;
import org.weda.internal.annotations.Service;

/**
 *
 * @author Mikhail Titov
 */
public class IncomingRtpStreamImpl extends AbstractRtpStream 
        implements IncomingRtpStream, ReceiveStreamListener
{
    public enum Status {INITIALIZING, OPENED, CLOSED}
    
    public final static AudioFormat FORMAT = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED) ;

    @Service
    private static RTPManagerService rtpManagerService;

    private RTPManager rtpManager;
    private SessionAddress destAddress;
    private ReceiveStream stream;
    private DataSource source; //SourceClonable
    private boolean firstConsumerAdded;
    private List<Consumer> consumers;
    private Lock lock;
    private Status status;

    public IncomingRtpStreamImpl(InetAddress address, int port)
    {
        super(address, port, "Incoming RTP");
        
        status = Status.INITIALIZING;
        consumers = new LinkedList<Consumer>();
        lock = new ReentrantLock();
        firstConsumerAdded = false;
    }

    public long getHandledBytes()
    {
        return 0;
    }

    public long getHandledPackets()
    {
        return 0;
    }

    @Override
    public void doRelease() throws Exception
    {
        try{
            try{
                if (!consumers.isEmpty())
                    for (Consumer consumer: consumers)
                        consumer.fireStreamClosingEvent();
            }finally{
                rtpManager.removeTargets("Disconnected");
            }
        }finally{
            rtpManager.dispose();
        }
    }

    public void open(String remoteHost) throws RtpStreamException
    {
        try
        {
            this.remoteHost = remoteHost;
            if (owner.isLogLevelEnabled(LogLevel.DEBUG))
                owner.getLogger().debug(logMess(
                        "Trying to open incoming RTP stream to the remote host (%s) using port (%s)"
                        , remoteHost, remotePort));

            rtpManager = rtpManagerService.createRtpManager();
            rtpManager.addReceiveStreamListener(this);
            rtpManager.initialize(new SessionAddress(address, port));
            InetAddress dest = InetAddress.getByName(remoteHost);
            destAddress = new SessionAddress(dest, SessionAddress.ANY_PORT);
            rtpManager.addTarget(destAddress);

        }
        catch(Exception e)
        {
            throw new RtpStreamException(logMess(
                        "Error creating receiver for RTP stream from remote host (%s)"
                        , remoteHost)
                    , e);
        }
    }

    public boolean addDataSourceListener(
            IncomingRtpStreamDataSourceListener listener, ContentDescriptor contentDescriptor)
        throws RtpStreamException
    {
        try{
            if (lock.tryLock(100, TimeUnit.MILLISECONDS)){
                try{
                    if (status==Status.CLOSED)
                        return false;
                    else if (status==Status.OPENED || status==Status.INITIALIZING){
                        Consumer consumer = new Consumer(listener, contentDescriptor);
                        consumers.add(consumer);
                        if (status==Status.OPENED)
                            consumer.fireDataSourceCreatedEvent();
                    } 
                }finally{
                    lock.unlock();
                }
                return true;
            } else
                return false;
        }catch(InterruptedException e){
            if (owner.isLogLevelEnabled(LogLevel.ERROR))
                owner.getLogger().error(logMess("Error adding listener"), e);
            throw new RtpStreamException("Error adding listener to the IncomingRtpStream", e);
        }
    }

    public void update(final ReceiveStreamEvent event)
    {
        if (owner.isLogLevelEnabled(LogLevel.DEBUG))
            owner.getLogger().debug(logMess("Received stream event (%s)", event.getClass().getName()));

        if (event instanceof NewReceiveStreamEvent)
        {
            stream = event.getReceiveStream();
            if (owner.isLogLevelEnabled(LogLevel.DEBUG))
                owner.getLogger().debug(logMess("Received new stream"));

            source = Manager.createCloneableDataSource(stream.getDataSource());

            RTPControl ctl = (RTPControl)source.getControl("javax.media.rtp.RTPControl");
            if (ctl!=null)
                if (owner.isLogLevelEnabled(LogLevel.DEBUG))
                    owner.getLogger().debug(logMess("The format of the stream: %s", ctl.getFormat()));

            lock.lock();
            try{
                if (!consumers.isEmpty())
                    for (Consumer consumer: consumers)
                        consumer.fireDataSourceCreatedEvent();
            }finally{
                lock.unlock();
            }
        }
        else if (event instanceof ByeEvent)
        {
            release();
        }
    }

    private void saveToFile(DataSource ds, String filename, final long closeAfter) throws Exception
    {
//
//            new Thread(){
//                @Override
//                public void run(){
//                    try{
//                        Thread.sleep(4000);
//                        DataSource ds = stream.getDataSource();
//
//                        RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
//                        if (ctl!=null)
//                            if (owner.isLogLevelEnabled(LogLevel.DEBUG))
//                                owner.getLogger().debug(logMess("The format of the stream: %s", ctl.getFormat()));
//
//                        // create a player by passing datasource to the Media Manager
//
//                        ds = javax.media.Manager.createCloneableDataSource(ds);
//                        SourceCloneable cloneable = (SourceCloneable) ds;
//                        saveToFile(ds,"test.wav", 10000);
//                        Thread.sleep(5000);
//                        saveToFile(cloneable.createClone(),"test2.wav", 5000);
//                    }
//                    catch(Exception e){
//                            owner.getLogger().error(logMess("Error."), e);
//                    }
//                }
//            }.start();

        Processor p = javax.media.Manager.createProcessor(ds);
        p.configure();
        waitForState(p, Processor.Configured);
        p.getTrackControls()[0].setFormat(new AudioFormat(
                AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED));
//        p.setContentDescriptor(new FileTypeDescriptor(FileTypeDescriptor.WAVE));
        p.realize();
        waitForState(p, Processor.Realized);
        final DataSink fileWriter = javax.media.Manager.createDataSink(
                p.getDataOutput(), new MediaLocator("file:///home/tim/tmp/"+filename));
        fileWriter.open();
        p.start();
        fileWriter.start();
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(closeAfter);
                    fileWriter.stop();
                    fileWriter.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void fireDataSourceCreated()
    {
        for (Consumer consumer: consumers)
            if (!consumer.createEventFired)
                consumer.fireDataSourceCreatedEvent();
    }

    private static void waitForState(Controller p, int state) throws Exception
    {
        long startTime = System.currentTimeMillis();
        while (p.getState()!=state)
        {
            TimeUnit.MILLISECONDS.sleep(5);
            if (System.currentTimeMillis()-startTime>200)
                throw new Exception("Processor state wait timeout");
        }
    }

    private class Consumer
    {
        private IncomingRtpStreamDataSourceListener listener;
        private boolean createEventFired = false;
        private Processor processor;
        private DataSource inDataSource;
        private DataSource outDataSource;
        private ContentDescriptor contentDescriptor;

        public Consumer(
                IncomingRtpStreamDataSourceListener listener, ContentDescriptor contentDescriptor)
        {
            this.listener = listener;
            this.contentDescriptor = contentDescriptor;
        }

        private void fireDataSourceCreatedEvent()
        {
            try{
                try{
                    inDataSource = !firstConsumerAdded? source : ((SourceCloneable)source).createClone();
                    firstConsumerAdded = true;

                    processor = Manager.createProcessor(inDataSource);
                    processor.configure();
                    waitForState(processor, Processor.Configured);
                    processor.getTrackControls()[0].setFormat(FORMAT);
                    processor.setContentDescriptor(contentDescriptor);
                    processor.realize();
                    waitForState(processor, Processor.Realized);
                    outDataSource = processor.getDataOutput();
                    processor.start();
//                    waitForState(processor, Processor.Started);

                    listener.dataSourceCreated(outDataSource);

                }catch(Exception e){
                    if (owner.isLogLevelEnabled(LogLevel.ERROR))
                        owner.getLogger().error(logMess(
                                "Error creating data source for consumer"), e);
                    listener.dataSourceCreated(null);
                }
            }finally{
                createEventFired = true;
            }
        }

        private void fireStreamClosingEvent()
        {
            try{
                try{
                    try{
                        try{
                            listener.streamClosing();
                        }finally{
                            inDataSource.stop();
                        }
                    }finally{
                        processor.stop();
                    }
                }finally{
                    outDataSource.stop();
                }
            }catch(Exception e){
                if (owner.isLogLevelEnabled(LogLevel.ERROR))
                    owner.getLogger().error(logMess(
                            "Error closing data source consumer resources"), e);
            }
        }
    }
}