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

package org.onesec.raven.ivr.actions;

import org.onesec.raven.ivr.IvrActionException;
import org.onesec.raven.ivr.IvrActionStatus;
import org.onesec.raven.ivr.IvrEndpoint;
import org.raven.log.LogLevel;

/**
 *
 * @author Mikhail Titov
 */
public class TransferCallAction extends AbstractAction
{
    public final static String ACTION_NAME = "Transfer call action";
    
    private final String address;

    public TransferCallAction(String address)
    {
        super(ACTION_NAME);
        this.address = address;
        setStatusMessage("Transfering call to the ("+address+") address");
    }

    public void execute(IvrEndpoint endpoint) throws IvrActionException
    {
        try
        {
            if (endpoint.isLogLevelEnabled(LogLevel.DEBUG))
                endpoint.getLogger().debug("Action. Transfering call to the ("+address+") address");
            endpoint.transfer(address);
        }
        finally
        {
            setStatus(IvrActionStatus.EXECUTED);
        }
    }

    public void cancel() throws IvrActionException
    {
    }
}
