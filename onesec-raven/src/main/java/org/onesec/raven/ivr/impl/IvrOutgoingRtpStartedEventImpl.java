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
package org.onesec.raven.ivr.impl;

import org.onesec.raven.ivr.AudioStream;
import org.onesec.raven.ivr.IvrEndpointConversation;
import org.onesec.raven.ivr.IvrOutgoingRtpStartedEvent;

/**
 *
 * @author Mikhail Titov
 */
public class IvrOutgoingRtpStartedEventImpl extends IvrEndpointConversationEventImpl implements IvrOutgoingRtpStartedEvent
{
    private final AudioStream audioStream;

    public IvrOutgoingRtpStartedEventImpl(IvrEndpointConversation conversation) {
        super(conversation);
        this.audioStream = conversation.getAudioStream();
    }

    public AudioStream getAudioStream() {
        return audioStream;
    }
}
