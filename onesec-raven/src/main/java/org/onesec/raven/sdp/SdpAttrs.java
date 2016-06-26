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
package org.onesec.raven.sdp;

/**
 *
 * @author Mikhail Titov
 */
public interface SdpAttrs {
    public final static String RTPMAP_ATTR = "rtpmap";
    public final static String RECVONLY_FLAG = "recvonly";
    public final static String SENDRECV_FLAG = "sendrecv";
    public final static String SENDONLY_FLAG = "sendonly";
    
    public boolean containsFlag(String name);
    public <T extends SdpAttr> T getAttr(String name);
    public void add(SdpAttr attr);
}