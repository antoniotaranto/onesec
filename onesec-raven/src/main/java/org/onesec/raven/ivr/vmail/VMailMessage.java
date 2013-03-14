/*
 * Copyright 2013 Mikhail Titov.
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
package org.onesec.raven.ivr.vmail;

import java.util.Date;
import org.raven.ds.InputStreamSource;

/**
 *
 * @author Mikhail Titov
 */
public interface VMailMessage {
    /**
     * Returns the sender phone number 
     */
    public String getSenderPhoneNumber();
    /**
     * Returns the message creation date
     */
    public Date getMessageDate();
    /**
     * Returns audio source of the message
     */
    public InputStreamSource getAudioSource();
    /**
     * Saves the message
     */
    public void save();
    /**
     * Deletes message
     */
    public void delete();
}
