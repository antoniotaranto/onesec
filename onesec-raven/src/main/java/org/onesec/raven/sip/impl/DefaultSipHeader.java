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
package org.onesec.raven.sip.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Mikhail Titov
 */
public class DefaultSipHeader extends AbstractMultiValueSipHeader<String> {

    public DefaultSipHeader(String name, List<String> values) {
        super(name, values);
    }

    @Override
    protected List<String> createValuesList(List<String> stringValues) {
        return Collections.unmodifiableList(new ArrayList<>(stringValues));
    }
}
