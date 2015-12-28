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

package org.onesec.raven.ivr.queue.actions;

import org.onesec.raven.ivr.IvrActionException;
import org.onesec.raven.ivr.actions.AbstractAction;
import org.onesec.raven.ivr.queue.QueuedCallStatus;

/**
 *
 * @author Mikhail Titov
 */
public class CancelQueueCallRequestAction extends AbstractAction
{
    public static final String ACTION_NAME = "Cancel queue call request";

    public CancelQueueCallRequestAction() {
        super(ACTION_NAME);
    }

    @Override
    protected ActionExecuted processExecuteMessage(Execute message) throws Exception {
        QueuedCallStatus state = (QueuedCallStatus) message.getConversation().getConversationScenarioState().getBindings().get(
                QueueCallAction.QUEUED_CALL_STATUS_BINDING);
        if (state!=null) {
            state.cancel();
            if (getLogger().isDebugEnabled())
                getLogger().debug("Queue call request was canceled");
        }
        return ACTION_EXECUTED_then_EXECUTE_NEXT;
    }

    @Override
    protected void processCancelMessage() throws Exception {
        sendExecuted(ACTION_EXECUTED_then_EXECUTE_NEXT);
    }

//    public void doExecute(IvrEndpointConversation conv) throws Exception {
//        QueuedCallStatus state = (QueuedCallStatus) conv.getConversationScenarioState().getBindings().get(
//                QueueCallAction.QUEUED_CALL_STATUS_BINDING);
//        if (state!=null) {
//            state.cancel();
//            if (logger.isDebugEnabled())
//                logger.debug("Queue call request was canceled");
//        }
//    }

    public void cancel() throws IvrActionException {
    }
}
