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

package org.onesec.raven.ivr.queue.event.impl;

import org.onesec.raven.ivr.queue.CallsQueue;
import org.onesec.raven.ivr.queue.event.OperatorQueueEvent;

/**
 *
 * @author Mikhail Titov
 */
public class OperatorQueueEventImpl extends CallQueueEventImpl implements OperatorQueueEvent
{
    private final String operatorId;
    private final String personId;
    private final String personDesc;

    public OperatorQueueEventImpl(CallsQueue callsQueue, long requestId, String operatorId
            , String personId, String personDesc)
    {
        super(callsQueue, requestId);
        this.operatorId = operatorId;
        this.personId = personId;
        this.personDesc = personDesc;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonDesc() {
        return personDesc;
    }

    public String getOperatorId() {
        return operatorId;
    }
}
