/*
 * Copyright 2012 Mikhail Titov.
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
package org.onesec.raven.net.sip_0.impl_0;

import org.onesec.raven.net.sip_0.SipAddressHeader;
import org.onesec.raven.net.sip_0.SipResponse;

/**
 *
 * @author Mikhail Titov
 */
public class SipResponseImpl extends AbstractSipMessage implements SipResponse {
    private final int statusCode;
    private final String reason;

    public SipResponseImpl(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

  public SipAddressHeader getFrom() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public SipAddressHeader getTo() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void getVia() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void getMaxForwards() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public SipAddressHeader getContact() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
